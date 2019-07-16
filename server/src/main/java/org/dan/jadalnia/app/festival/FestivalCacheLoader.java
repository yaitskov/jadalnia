package org.dan.jadalnia.app.festival;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static org.dan.jadalnia.app.festival.Festival.TID;
import static org.dan.jadalnia.sys.db.DbContext.TRANSACTION_MANAGER;
import static org.dan.jadalnia.sys.error.JadalniaEx.notFound;

import com.google.common.cache.CacheLoader;
import lombok.extern.slf4j.Slf4j;
import org.dan.jadalnia.app.bid.Bid;
import org.dan.jadalnia.app.category.CategoryMemState;
import org.dan.jadalnia.app.category.Cid;
import org.dan.jadalnia.app.group.Gid;
import org.dan.jadalnia.app.order.MatchInfo;
import org.dan.jadalnia.app.order.Oid;
import org.dan.jadalnia.app.playoff.PowerRange;
import org.dan.jadalnia.util.collection.MaxValue;
import org.dan.jadalnia.util.counter.BidSeqGen;
import org.dan.jadalnia.util.counter.CidSeqGen;
import org.dan.jadalnia.util.counter.DidSeqGen;
import org.dan.jadalnia.util.counter.GidSeqGen;
import org.dan.jadalnia.util.counter.OidSeqGen;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;


@Slf4j
public class FestivalCacheLoader extends CacheLoader<Fid, CompletableFuture<Festival>>  {
    public static final String TOURNAMENT_NOT_FOUND = "tournament not found";

    @Inject
    private FestivalDao festivalDao;

    @Override
    @Transactional(readOnly = true, transactionManager = TRANSACTION_MANAGER)
    public CompletableFuture<Festival> load(Fid fid) throws Exception {
        log.info("Loading tournament {}", fid);
        final TournamentRow row = festivalDao.getRow(fid)
                .orElseThrow(() -> notFound(TOURNAMENT_NOT_FOUND, TID, fid));
        final MaxValue<Oid> maxMid = new MaxValue<>(Oid.of(1));
        final MaxValue<Bid> maxBid = new MaxValue<>(Bid.of(1));
        final Map<Oid, MatchInfo> matchMap = combineMatchesAndSets(
                matchDao.load(fid, maxMid),
                matchScoreDao.load(fid));
        final MaxValue<Cid> maxCid = new MaxValue<>(Cid.of(1));
        final MaxValue<Gid> maxGid = new MaxValue<>(Gid.of(1));
        final Map<Bid, ParticipantMemState> participants = bidDao.loadParticipants(fid, maxBid);
        return Festival.builder()
                .name(row.getName())
                .condActions(OneTimeCondActions
                        .builder()
                        .onScheduleTables(new ArrayList<>())
                        .build())
                .powerRange(new PowerRange())
                .type(row.getType())
                .uidCid2Bid(participants.values().stream()
                        .collect(groupingBy(ParticipantMemState::getUid,
                                toMap(ParticipantMemState::getCid,
                                        ParticipantMemState::getBid))))
                .participants(participants)
                .categories(categoryDao.listCategoriesByTid(fid).stream()
                        .peek(c -> maxCid.accept(c.getCid()))
                        .collect(toMap(CategoryMemState::getCid, o -> o)))
                .groups(groupDao.load(fid, maxGid))
                .nextCategory(new CidSeqGen(maxCid.getMax()))
                .nextGroup(new GidSeqGen(maxGid.getMax()))
                .nextDispute(new DidSeqGen(0)) // to be load
                .nextMatch(new OidSeqGen(maxMid.getMax()))
                .nextBid(new BidSeqGen(maxBid.getMax()))
                .matches(matchMap)
                .tid(fid)
                .sport(row.getSport())
                .adminIds(festivalDao.loadAdmins(fid))
                .state(row.getState())
                .ticketPrice(row.getTicketPrice())
                .opensAt(row.getStartedAt())
                .previousTid(row.getPreviousTid())
                .completeAt(row.getEndedAt())
                .pid(row.getPid())
                .rule(row.getRules())
                .build();
    }

    private Map<Oid, MatchInfo> combineMatchesAndSets(List<MatchInfo> matches,
            Map<Oid, Map<Bid, List<Integer>>> sets) {
        return matches.stream().collect(
                toMap(MatchInfo::getMid,
                        m -> {
                            ofNullable(sets.get(m.getMid()))
                                    .ifPresent(psets -> psets.forEach((bid, usets) ->
                                            m.getParticipantIdScore().put(bid, usets)));
                            return m;
                        }));
    }
}
