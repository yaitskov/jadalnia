package org.dan.jadalnia.app.festival;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.dan.jadalnia.app.auth.AuthService.SESSION;
import static org.dan.jadalnia.app.bid.BidState.Quit;
import static org.dan.jadalnia.app.bid.SelectedBid.selectBid;
import static org.dan.jadalnia.app.category.CategoryResource.CID;
import static org.dan.jadalnia.app.category.CategoryResource.CID_JP;
import static org.dan.jadalnia.app.order.OrderResource.TID_JP;
import static org.dan.jadalnia.app.festival.FestivalCache.FESTIVAL_CACHE;
import static org.dan.jadalnia.app.festival.Festival.TID;
import static org.dan.jadalnia.sys.error.JadEx.badRequest;
import static org.dan.jadalnia.sys.error.JadEx.forbidden;

import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import lombok.extern.slf4j.Slf4j;
import org.dan.jadalnia.app.auth.AuthService;
import org.dan.jadalnia.app.bid.BidState;
import org.dan.jadalnia.app.bid.Uid;
import org.dan.jadalnia.app.category.Cid;
import org.dan.jadalnia.app.user.UserInfo;
import org.dan.jadalnia.app.user.UserSession;
import org.dan.jadalnia.sys.validation.FidBodyRequired;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;

@Slf4j
@Path("/")
@Produces(APPLICATION_JSON)
public class FestivalResource {
    private static final String FESTIVAL = "/festival/";
    public static final String RUNNING_TOURNAMENTS = FESTIVAL + "running";
    public static final String FESTIVAL_STATE = FESTIVAL + "state";
    public static final String BEGIN_TOURNAMENT = FESTIVAL + "begin";
    public static final String CANCEL_TOURNAMENT = FESTIVAL + "cancel";
    public static final String DRAFTING = "tournament/drafting/";
    public static final String MY_TOURNAMENT = "/tournament/mine/";
    public static final String TOURNAMENT_RULES = FESTIVAL + "rules";
    public static final String GET_TOURNAMENT_RULES = TOURNAMENT_RULES + "/";
    public static final String FESTIVAL_CREATE = FESTIVAL + "create";
    public static final String TOURNAMENT_INVALIDATE_CACHE = "/tournament/invalidate/cache";
    public static final String TOURNAMENT_ENLIST_OFFLINE = FESTIVAL + "enlist-offline";
    public static final String TOURNAMENT_RESIGN = "/tournament/resign";
    public static final String TOURNAMENT_EXPEL = FESTIVAL + "expel";
    public static final String TOURNAMENT_UPDATE = FESTIVAL + "update";
    public static final String TOURNAMENT_ENLISTED = "/tournament/enlisted";
    public static final String MY_RECENT_TOURNAMENT = "/tournament/my-recent";
    public static final String MY_RECENT_TOURNAMENT_JUDGEMENT =  FESTIVAL + "my-recent-judgement";
    public static final String TOURNAMENT_RESULT = "/tournament/result/";
    public static final String RESULT_CATEGORY = "/category/";
    public static final String TOURNAMENT_COMPLETE = "/tournament/complete/";
    public static final String TOURNAMENT_PLAY_OFF_MATCHES = "/tournament/play-off-matches/";
    public static final String TOURNAMENT_FOLLOWING = "/tournament/following/";

    @Inject
    private FestivalService festivalService;
    
    @Inject
    private AuthService authService;
    

    @POST
    @Path(FESTIVAL_CREATE)
    @Consumes(APPLICATION_JSON)
    public CreatedFestival create(NewFestival newFestival) {
        return festivalService.create(newFestival);
    }

    @POST
    @Path(FESTIVAL_STATE)
    @Consumes(APPLICATION_JSON)
    public void setState(
            @HeaderParam(SESSION) UserSession session,
            FestivalState state) {
        final Fid fid = authService.find(session).getFid();
        festivalService.setState(fid, state);
    }

    @GET
    @Path(FESTIVAL + "/menu/" + "{fid}")
    public CompletableFuture<List<MenuItem>> listMenu(@PathParam("fid") Fid fid) {
        return festivalService.listMenu(fid);
    }

    @POST
    @Path(FESTIVAL + "/menu")
    public void updateMenu(
            @HeaderParam(SESSION) UserSession session,
            List<MenuItem> items) {
        final Fid fid = authService.find(session).getFid();
        festivalService.updateMenu(fid, items);
    }

    @Inject
    @Named(FESTIVAL_CACHE)
    private LoadingCache<Fid, Festival> festivalCache;

    @POST
    @Path(TOURNAMENT_INVALIDATE_CACHE)
    @Consumes(APPLICATION_JSON)
    public void invalidateCache(
            @Suspended AsyncResponse response,
            @HeaderParam(SESSION) String session,
            @FidBodyRequired @Valid Fid fid) {
        final Uid adminUid = authService.find(session).getUid();
        tournamentAccessor.update(fid, response, (tournament, batch) -> {
            tournament.checkAdmin(adminUid);
            log.info("invalidate tournament cache {}", fid);
            festivalCache.invalidate(fid);
        });
    }

    @POST
    @Path(TOURNAMENT_ENLIST_OFFLINE)
    @Consumes(APPLICATION_JSON)
    public void enlistOffline(
            @Suspended AsyncResponse response,
            @HeaderParam(SESSION) String session,
            @Valid EnlistOffline enlistment) {
        final Uid adminUid = authService.find(session).getUid();
        tournamentAccessor.update(enlistment.getTid(), response, (tournament, batch) -> {
            tournament.checkAdmin(adminUid);
            return festivalService.enlistOffline(adminUid, tournament, enlistment, batch);
        });
    }

    @POST
    @Path(TOURNAMENT_UPDATE)
    @Consumes(APPLICATION_JSON)
    public void update(
            @Suspended AsyncResponse response,
            @HeaderParam(SESSION) String session,
            TournamentUpdate update) {
        final Uid adminUid = authService.find(session).getUid();
        tournamentAccessor.update(update.getTid(), response, (tournament, batch) -> {
            tournament.checkAdmin(adminUid);
            festivalService.update(tournament, update, batch);
        });
    }

    @POST
    @Path(TOURNAMENT_RESIGN)
    @Consumes(APPLICATION_JSON)
    public void resign(
            @HeaderParam(SESSION) String session,
            @Suspended AsyncResponse response,
            ResignTournament resign) {
        final Uid uid = authService.find(session).getUid();
        tournamentAccessor.update(resign.getTid(), response, (tournament, batch) -> {
            tournament.findBidsByUid(uid).stream()
                    .map(tournament::getParticipant)
                    .filter(p -> resign.getCid()
                            .map(cid -> cid.equals(p.getCid())).orElse(true))
                    .forEach(bid -> festivalService.leaveTournament(
                            selectBid(tournament, bid, batch), Quit));
        });
    }

    @Inject
    private TournamentAccessor tournamentAccessor;

    private static final Set<BidState> acceptableBidExpelTargetStates
            = ImmutableSet.of(BidState.Expl, Quit);

    @POST
    @Path(TOURNAMENT_EXPEL)
    @Consumes(APPLICATION_JSON)
    public void expel(
            @Suspended AsyncResponse response,
            @HeaderParam(SESSION) String session,
            @Valid ExpelParticipant expelParticipant) {
        if (!acceptableBidExpelTargetStates.contains(expelParticipant.getTargetBidState())) {
            throw badRequest("bid target state is out of range");
        }
        final Uid uid = authService.find(session).getUid();
        tournamentAccessor.update(expelParticipant.getTid(), response, (tournament, batch) -> {
            tournament.checkAdmin(uid);
            festivalService.leaveTournament(
                    selectBid(tournament, expelParticipant.getBid(), batch),
                    expelParticipant.getTargetBidState());
        });
    }

    @GET
    @Path(TOURNAMENT_ENLISTED)
    @Consumes(APPLICATION_JSON)
    public List<TournamentDigest> findTournamentsAmGoingTo(
            @HeaderParam(SESSION) String session) {
        return findTournamentsAmGoingTo(session, 0);
    }

    @GET
    @Path(TOURNAMENT_ENLISTED + "/{days}")
    @Consumes(APPLICATION_JSON)
    public List<TournamentDigest> findTournamentsAmGoingTo(
            @HeaderParam(SESSION) String session,
            @PathParam("days") int days) {
        return festivalService.findInWithEnlisted(
                authService.find(session).getUid(), days);
    }

    @GET
    @Path(DRAFTING)
    @Consumes(APPLICATION_JSON)
    public List<DatedTournamentDigest> findDrafting() {
        return festivalService.findDrafting();
    }

    @GET
    @Path(RUNNING_TOURNAMENTS)
    @Consumes(APPLICATION_JSON)
    public List<OpenTournamentDigest> findRunning() {
        return findRunning(0);
    }

    @GET
    @Path(RUNNING_TOURNAMENTS + "/{days}")
    @Consumes(APPLICATION_JSON)
    public List<OpenTournamentDigest> findRunning(@PathParam("days") int days) {
        return festivalService.findRunning(days);
    }

    @GET
    @Path(DRAFTING + TID_JP)
    @Consumes(APPLICATION_JSON)
    public void getDraftingTournament(
            @Suspended AsyncResponse response,
            @HeaderParam(SESSION) String session,
            @PathParam(TID) Fid fid) {
        tournamentAccessor.read(fid, response,
                tournament -> festivalService.getDraftingTournament(tournament,
                        authService.userInfoBySessionQuite(session)
                                .map(UserInfo::getUid)));
    }

    @GET
    @Path(MY_TOURNAMENT + TID_JP)
    @Consumes(APPLICATION_JSON)
    public void getMyTournamentInfo(
            @Suspended AsyncResponse response,
            @PathParam(TID) Fid fid) {
        tournamentAccessor.read(fid, response,
                tournament -> festivalService.getMyTournamentInfo(tournament));
    }

    @GET
    @Path(GET_TOURNAMENT_RULES + TID_JP)
    @Consumes(APPLICATION_JSON)
    public void getTournamentRules(
            @Suspended AsyncResponse response,
            @PathParam(TID) Fid fid) {
        tournamentAccessor.read(fid, response, Festival::getRule);
    }

    @POST
    @Path(TOURNAMENT_RULES)
    @Consumes(APPLICATION_JSON)
    public void updateTournamentRules(
            @Suspended AsyncResponse response,
            @HeaderParam(SESSION) String session,
            TidIdentifiedRules parameters) {
        rulesValidator.validate(parameters.getRules());
        Uid uid = authService.find(session).getUid();
        tournamentAccessor.update(parameters.getTid(), response, (tournament, batch) -> {
            tournament.checkAdmin(uid);
            festivalService.updateTournamentParams(tournament, parameters, batch);
        });
    }

    @POST
    @Path(BEGIN_TOURNAMENT)
    @Consumes(APPLICATION_JSON)
    public void beginTournament(
            @Suspended AsyncResponse response,
            @HeaderParam(SESSION) String session,
            int tid) {
        final Uid uid = authService.find(session).getUid();
        log.info("Uid {} begins tid {}", uid, tid);
        tournamentAccessor.update(new Fid(tid), response, (tournament, batch) -> {
            tournament.checkAdmin(uid);
            festivalService.beginAndSchedule(tournament, batch);
        });
    }

    @POST
    @Path(CANCEL_TOURNAMENT)
    @Consumes(APPLICATION_JSON)
    public void cancelTournament(
            @Suspended AsyncResponse response,
            @HeaderParam(SESSION) String session,
            int tid) {
        final Uid uid = authService.find(session).getUid();
        log.info("User {} tried to cancel tournament {}", uid, tid);
        tournamentAccessor.update(new Fid(tid), response, (tournament, batch) -> {
            tournament.checkAdmin(uid);
            festivalService.cancel(tournament, batch);
        });
    }

    @Inject
    private TournamentTerminator tournamentTerminator;

    
    @GET
    @Path(MY_RECENT_TOURNAMENT)
    public MyRecentTournaments findMyRecentPlayedTournaments(
            @HeaderParam(SESSION) String session) {
        final Uid uid = authService.find(session).getUid();
        return festivalService.findMyRecentTournaments(uid);
    }

    @GET
    @Path(MY_RECENT_TOURNAMENT_JUDGEMENT)
    public MyRecentJudgedTournaments findMyRecentJudgedTournaments(
            @HeaderParam(SESSION) String session) {
        final Uid uid = authService.find(session).getUid();
        return festivalService.findMyRecentJudgedTournaments(uid);
    }

    @GET
    @Path(TOURNAMENT_RESULT + TID_JP + RESULT_CATEGORY + CID_JP)
    public void tournamentResult(
            @Suspended AsyncResponse response,
            @PathParam(TID) Fid fid,
            @PathParam(CID) Cid cid) {
        tournamentAccessor.read(fid, response,
                (tournament) -> festivalService.tournamentResult(tournament, cid));
    }

    @GET
    @Path(TOURNAMENT_COMPLETE + TID_JP)
    public void completeInfo(
            @Suspended AsyncResponse response,
            @PathParam(TID) Fid fid) {
        tournamentAccessor.read(fid, response,
                (tournament) -> festivalService.completeInfo(tournament));
    }

    @GET
    @Path(TOURNAMENT_PLAY_OFF_MATCHES + TID_JP + "/" + CID_JP)
    public void playOffMatches(
            @Suspended AsyncResponse response,
            @PathParam(TID) Fid fid,
            @PathParam(CID) Cid cid) {
        tournamentAccessor.read(fid, response,
                (tournament) -> festivalService.playOffMatches(tournament, cid));
    }

    @GET
    @Path(TOURNAMENT_FOLLOWING + TID_JP)
    public List<TournamentDigest> following(@Valid @PathParam(TID) Fid fid) {
        return festivalService.findFollowingFrom(fid);
    }
}
