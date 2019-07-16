package org.dan.jadalnia.app.order.dispute;

import static org.dan.jadalnia.app.order.dispute.DisputeStatus.CLAIMED;
import static org.dan.jadalnia.app.festival.FestivalState.Open;
import static org.dan.jadalnia.sys.error.JadalniaEx.badRequest;

import org.dan.jadalnia.app.bid.Bid;
import org.dan.jadalnia.app.bid.Uid;
import org.dan.jadalnia.app.order.MatchInfo;
import org.dan.jadalnia.app.festival.Festival;
import org.dan.jadalnia.sys.db.DbUpdater;
import org.dan.jadalnia.util.time.Clocker;

import java.time.Instant;
import java.util.Optional;

import javax.inject.Inject;

public class MatchDisputeService {
    @Inject
    private MatchDisputeDao matchDisputeDao;
    @Inject
    private Clocker clocker;

    public DisputeId openDispute(Festival tournament,
            DisputeClaimRequest claim, DbUpdater batch, Uid uid) {
        MatchInfo m = tournament.getMatchById(claim.getMid());
        final Bid bid = tournament.findBidByMidAndUid(m, uid);
        m.checkParticipant(bid);

        validate(tournament, claim, bid);
        final Instant now = clocker.get();
        final DisputeMemState dispute = DisputeMemState
                .builder()
                .created(now)
                .status(CLAIMED)
                .did(tournament.getNextDispute().next())
                .mid(claim.getMid())
                .plaintiff(bid)
                .plaintiffComment(claim.getComment())
                .judgeComment(Optional.empty())
                .judge(Optional.empty())
                .proposedScore(claim.getSets())
                .resolvedAt(Optional.empty())
                .build();
        matchDisputeDao.create(tournament.getTid(), dispute, batch);

        tournament.getDisputes().add(dispute);
        return dispute.getDid();
    }

    private void validate(Festival tournament,
            DisputeClaimRequest claim, Bid bid) {
        claim.getComment().filter(c -> c.length() < 40)
                .ifPresent(c -> {
                    throw badRequest("comment-longer-than", "n", 40);
                });
        tournament.checkState(Open);
        if (tournament.getDisputes().stream()
                .filter(d -> d.getPlaintiff().equals(bid)).count() > 10) {
            throw badRequest("tournament-dispute-limit");
        }
    }
}
