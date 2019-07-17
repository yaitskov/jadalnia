package org.dan.jadalnia.app.order;

import static java.util.Collections.singleton;
import static org.dan.jadalnia.app.bid.BidState.Lost;
import static org.dan.jadalnia.app.bid.BidState.Play;
import static org.dan.jadalnia.app.bid.BidState.TERMINAL_RECOVERABLE_STATES;
import static org.dan.jadalnia.app.bid.BidState.Wait;
import static org.dan.jadalnia.app.bid.BidState.Win1;
import static org.dan.jadalnia.app.bid.BidState.Win2;
import static org.dan.jadalnia.app.bid.BidState.Win3;
import static org.dan.jadalnia.app.category.CategoryState.End;
import static org.dan.jadalnia.app.category.CategoryState.Ply;
import static org.dan.jadalnia.app.order.MatchState.Auto;
import static org.dan.jadalnia.app.order.MatchState.Draft;
import static org.dan.jadalnia.app.order.MatchState.Game;
import static org.dan.jadalnia.app.order.MatchState.Over;
import static org.dan.jadalnia.app.order.MatchState.Place;
import static org.dan.jadalnia.app.order.dispute.MatchSets.ofSets;
import static org.dan.jadalnia.app.festival.FestivalState.Close;
import static org.dan.jadalnia.app.festival.FestivalState.Open;
import static org.dan.jadalnia.sys.error.JadEx.badRequest;
import static org.dan.jadalnia.sys.error.JadEx.internalError;

import com.google.common.collect.ImmutableSet;
import lombok.extern.slf4j.Slf4j;
import org.dan.jadalnia.app.bid.Bid;
import org.dan.jadalnia.app.bid.BidRemover;
import org.dan.jadalnia.app.bid.BidService;
import org.dan.jadalnia.app.bid.BidState;
import org.dan.jadalnia.app.category.CategoryMemState;
import org.dan.jadalnia.app.category.CategoryService;
import org.dan.jadalnia.app.category.Cid;
import org.dan.jadalnia.app.group.GroupService;
import org.dan.jadalnia.app.order.dispute.MatchSets;
import org.dan.jadalnia.app.playoff.PlayOffService;
import org.dan.jadalnia.app.sched.ScheduleServiceSelector;
import org.dan.jadalnia.app.sport.Sports;
import org.dan.jadalnia.app.festival.ParticipantMemState;
import org.dan.jadalnia.app.festival.Festival;
import org.dan.jadalnia.app.festival.FestivalService;
import org.dan.jadalnia.app.festival.FestivalState;
import org.dan.jadalnia.app.festival.TournamentTerminator;
import org.dan.jadalnia.app.festival.rel.RelatedTournamentsService;
import org.dan.jadalnia.sys.db.DbUpdater;
import org.dan.jadalnia.util.time.Clocker;

import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

@Slf4j
public class MatchEditorService {
    public static final ImmutableSet<BidState> RESETABLE_BID_STATES = ImmutableSet
            .of(Lost, Win1, Win2, Win3);
    private static final Set<MatchState> GAME_PLACE_EXPECTED = ImmutableSet.of(Place, Game);
    private static final Set<MatchState> OVER_EXPECTED = singleton(Over);

    @Inject
    private MatchDao matchDao;

    @Inject
    private GroupService groupService;

    @Inject
    private PlayOffService playOffService;

    @Inject
    private OrderService orderService;

    @Inject
    private BidService bidService;

    @Inject
    private Clocker clocker;

    @Inject
    private ScheduleServiceSelector scheduleService;

    @Inject
    private FestivalService festivalService;

    public void rescoreMatch(Festival tournament, RescoreMatch rescore, DbUpdater batch) {
        final MatchInfo rescoringMatch = tournament.getMatchById(rescore.getMid());
        final MatchSets newSets = ofSets(rescore.getSets());
        validateRescoreMatch(tournament, rescoringMatch, newSets);
        final AffectedMatches affectedMatches = affectedMatchesService
                .findEffectedMatches(tournament, rescoringMatch, newSets);
        affectedMatchesService.validateEffectHash(tournament, rescore, affectedMatches);

        final Optional<Bid> newWinner = sports.findNewWinnerBid(
                tournament, newSets, rescoringMatch);
        log.info("New winner {} in mid {}", newWinner, rescoringMatch.getMid());
        reopenTournamentIfOpenMatch(
                tournament, batch, affectedMatches, newWinner, rescoringMatch.getCid());
        overrideMatchSets(batch, rescoringMatch, newSets);

        if (newWinner.isPresent()) {
            matchRescoreGivesWinner(tournament, batch, rescoringMatch, newWinner, affectedMatches);
        } else {
            matchRescoreNoWinner(tournament, batch, rescoringMatch, affectedMatches);
        }
        scheduleService.afterMatchComplete(tournament, batch, clocker.get());
    }

    private void overrideMatchSets(DbUpdater batch, MatchInfo rescoringMatch, MatchSets newSets) {
        truncateSets(batch, rescoringMatch, 0);
        rescoringMatch.loadParticipants(newSets);
        matchDao.insertScores(rescoringMatch, batch);
    }

    private void matchRescoreNoWinner(Festival tournament, DbUpdater batch,
            MatchInfo mInfo, AffectedMatches affectedMatches) {
        if (mInfo.getState() == Game) {
            log.info("Mid {} stays open", mInfo.getMid());
        } else { // request table scheduling
            removeWinnerUidIf(batch, mInfo);
            applyAffects(tournament, batch, affectedMatches);
            log.info("Rescored mid {} returns to game", mInfo.getMid());
            mInfo.participants()
                    .map(tournament::getBidOrQuit)
                    .forEach(bid -> resetBidStateTo(batch, bid,
                            orderService.completeGroupMatchBidState(tournament, bid)));
            resetBidStatesForRestGroupParticipants(tournament, mInfo, batch);
            orderService.changeStatus(batch, mInfo, Place);
        }
    }

    private void removeWinnerUidIf(DbUpdater batch, MatchInfo mInfo) {
        mInfo.getWinnerId().ifPresent(wId -> {
            mInfo.setWinnerId(Optional.empty());
            matchDao.setWinnerId(mInfo, batch);
        });
    }

    private void resetBidStatesForRestGroupParticipants(Festival tournament,
            MatchInfo mInfo, DbUpdater batch) {
        mInfo.getGid().ifPresent(gid -> {
            log.info("Reset rest lost bids to wait in gid {} of tid {}", gid, tournament.getTid());
            tournament.getParticipants().values().stream()
                    .filter(p -> p.getGid().equals(mInfo.getGid()))
                    .filter(p -> RESETABLE_BID_STATES.contains(p.getBidState()))
                    .forEach(p -> bidService.setBidState(p, Wait, RESETABLE_BID_STATES, batch));
        });
    }

    private void matchRescoreGivesWinner(Festival tournament, DbUpdater batch,
            MatchInfo mInfo, Optional<Bid> newWinner, AffectedMatches affectedMatches) {
        if (matchHadWinner(mInfo)) {
            removeWinnerUidIf(batch, mInfo);
            applyAffects(tournament, batch, affectedMatches);
            makeParticipantPlaying(tournament, batch, mInfo);
            resetBidStatesForRestGroupParticipants(tournament, mInfo, batch);
            orderService.matchWinnerDetermined(
                    tournament, mInfo, newWinner.get(), batch, OVER_EXPECTED);
        } else { // was playing
            log.info("Rescored mid {} is complete", mInfo.getMid());
            orderService.matchWinnerDetermined(
                    tournament, mInfo, newWinner.get(), batch, GAME_PLACE_EXPECTED);
        }
    }

    private boolean matchHadWinner(MatchInfo mInfo) {
        return mInfo.getWinnerId().isPresent();
    }

    private void makeParticipantPlaying(Festival tournament,
            DbUpdater batch, MatchInfo mInfo) {
        mInfo.participants()
                .map(tournament::getBidOrQuit)
                .forEach(bid -> {
                    if (bid.state() != Play) {
                        resetBidStateTo(batch, bid, Play);
                    }
                });
    }

    @Inject
    private TournamentTerminator tournamentTerminator;

    @Inject
    private CategoryService categoryService;

    private void reopenTournamentIfOpenMatch(Festival tournament, DbUpdater batch,
            AffectedMatches affectedMatches, Optional<Bid> newWinner, Cid cid) {
        final CategoryMemState cat = tournament.getCategory(cid);
        if (cat.getState() == End) {
            if (!affectedMatches.getToBeCreatedDm().isEmpty()
                    || !affectedMatches.getToBeReset().isEmpty()
                    || !newWinner.isPresent()) {
                categoryService.setState(tournament.getTid(), cat, Ply, batch);
                if (tournament.getState() == Close) {
                    tournamentTerminator.setTournamentState(tournament, Open, batch);
                }
            }
        }
    }

    @Inject
    private AffectedMatchesService affectedMatchesService;

    private static final Set<FestivalState> openOrClose = ImmutableSet.of(Open, Close);

    @Inject
    private Sports sports;

    private void validateRescoreMatch(Festival tournament, MatchInfo mInfo,
            MatchSets newSets) {
        if (!openOrClose.contains(tournament.getState())) {
            throw badRequest("tournament is not open nor closed");
        }
        if (mInfo.playedSets() == 0) {
            throw badRequest("match should have a scored set");
        }
        newSets.validateParticipants(mInfo.getParticipantIdScore().keySet());
        if (newSets.validateEqualNumberSets() == 0) {
            throw badRequest("new match score has no any set");
        }

        final MatchInfo mInfoExpectedAfter = sports.alternativeSetsWithoutWinner(mInfo, newSets);
        sports.validateMatch(tournament, mInfoExpectedAfter);
        sports.checkWonSets(tournament.selectMatchRule(mInfo),
                sports.calcWonSets(tournament, mInfoExpectedAfter));
    }

    public void resetMatchScore(
            Festival tournament, ResetSetScore reset, DbUpdater batch) {
        final MatchInfo mInfo = tournament.getMatchById(reset.getMid());
        final int numberOfSets = mInfo.playedSets();
        if (numberOfSets < reset.getSetNumber()) {
            throw badRequest("Match has just " + numberOfSets + " sets");
        }
        if (numberOfSets == reset.getSetNumber()) {
            return;
        }
        if (mInfo.getState() == Game) {
            truncateSets(batch, mInfo, reset.getSetNumber());
            return;
        }

        resetScoreOfCompleteMatch(tournament, reset, batch, mInfo);
    }

    @Inject
    private RelatedTournamentsService relatedTournaments;

    public void resetScoreOfCompleteMatch(
            Festival tournament, ResetSetScore reset,
            DbUpdater batch, MatchInfo mInfo) {
        final MatchSets newSets = mInfo.sliceFirstSets(reset.getSetNumber());
        final AffectedMatches affectedMatches = affectedMatchesService
                .findEffectedMatches(tournament, mInfo, newSets);
        truncateSets(batch, mInfo, reset.getSetNumber());
        applyAffects(tournament, batch, affectedMatches);
        orderService.changeStatus(batch, mInfo, Game);

        applyAffects(tournament, batch, affectedMatches);

        reopenTournamentIfOpenMatch(tournament, batch, affectedMatches,
                Optional.empty(), mInfo.getCid());
        scheduleService.afterMatchComplete(tournament, batch, clocker.get());
    }

    @Inject
    private MatchRemover matchRemover;

    @Inject
    private BidRemover bidRemover;

    private void applyAffects(Festival tournament, DbUpdater batch,
            AffectedMatches affectedMatches) {
        affectedMatches.getToBeReset().forEach(
                aMatch -> removeParticipant(
                        tournament, batch,
                        tournament.getMatchById(aMatch.getMid()),
                        aMatch.getBid()));
        matchRemover.deleteByMids(tournament, batch, affectedMatches.getToBeRemovedDm());

        affectedMatches.getToBeCreatedDm().forEach(
                mp -> groupService.createDisambiguateMatches(batch, tournament,
                        tournament.getParticipant(mp.getBidLess()).gid(), mp));

        affectedMatches.getLineUpDiff().ifPresent(lud ->
                bidRemover.removeByBids(tournament, batch, lud.toBeUnlisted()));

        affectedMatches.getConsoleAffect().forEach((conTid, affect) -> {
            log.info("Apply affects on console tournament {} of {}",
                    conTid, tournament.getTid());
            applyAffects(relatedTournaments.loadTournament(conTid), batch, affect);
        });
    }

    private void removeParticipant(Festival tournament,
            DbUpdater batch, MatchInfo mInfo, Bid bid) {
        final int played = mInfo.playedSets();
        if (!mInfo.removeParticipant(bid)) {
            log.warn("No uid {} is not in mid {}", bid, mInfo.getMid());
            return;
        }
        matchDao.removeScores(batch, mInfo, bid, played);
        mInfo.leftBid().ifPresent(obid -> {
            matchDao.removeScores(batch, mInfo, obid, played);
            mInfo.getParticipantScore(obid).clear();
        });
        final int numberOfParticipants = mInfo.numberOfParticipants();
        if (numberOfParticipants == 1) {
            log.warn("Remove first uid {} from mid {}", bid, mInfo.getMid());
            final Bid opBid = mInfo.leftBid().get();
            final ParticipantMemState opponent = tournament.getBidOrQuit(opBid);
            final BidState opoState = opponent.state();
            switch (opoState) {
                case Expl:
                case Quit:
                    orderService.changeStatus(batch, mInfo, Auto);
                    break;
                default:
                    orderService.changeStatus(batch, mInfo, Draft);
                    break;
            }
            resetBidStateTo(batch, opponent, Wait);
            resetBidStateTo(batch, tournament.getBidOrQuit(bid), Wait);
            removeWinnerUidIf(batch, mInfo);
            matchDao.removeSecondParticipant(batch, mInfo, opBid);
        } else if (numberOfParticipants == 0) {
            log.warn("Remove last uid {} from mid {}", bid, mInfo.getMid());
            orderService.changeStatus(batch, mInfo, Draft);
            matchDao.removeParticipants(batch, mInfo);
        } else {
            throw internalError("unexpected number or participants left "
                    + numberOfParticipants);
        }
    }

    private void resetBidStateTo(DbUpdater batch, ParticipantMemState opponent,
            BidState targetState) {
        if (TERMINAL_RECOVERABLE_STATES.contains(opponent.state())) {
            bidService.setBidState(opponent, targetState,
                    TERMINAL_RECOVERABLE_STATES, batch);
        }
    }

    private void truncateSets(DbUpdater batch, MatchInfo minfo, int setNumber) {
        matchDao.deleteSets(batch, minfo, setNumber);
        cutTrailingSets(minfo, setNumber);
    }

    private void cutTrailingSets(MatchInfo minfo, int setNumber) {
        minfo.getParticipantIdScore().values()
                .forEach(scores -> scores.subList(setNumber, scores.size()).clear());
    }
}
