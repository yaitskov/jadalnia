package org.dan.jadalnia.app.festival;

import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.dan.jadalnia.app.festival.menu.MenuItem;
import org.dan.jadalnia.app.festival.pojo.FestivalInfo;
import org.dan.jadalnia.app.festival.pojo.FestivalState;
import org.dan.jadalnia.app.festival.pojo.Fid;
import org.dan.jadalnia.app.festival.pojo.NewFestival;
import org.jooq.DSLContext;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static java.util.Optional.ofNullable;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.dan.jadalnia.jooq.Tables.FESTIVAL;
import static org.dan.jadalnia.sys.db.DbContext.TRANSACTION_MANAGER;
import static org.dan.jadalnia.sys.error.JadEx.internalError;
import static org.dan.jadalnia.sys.error.JadEx.notFound;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FestivalDao {
    DSLContext jooq;
    ExecutorService executor;

    public CompletableFuture<Fid> create(NewFestival newTournament) {
        return supplyAsync(() -> jooq.insertInto(FESTIVAL,
                FESTIVAL.STATE,
                FESTIVAL.OPENS_AT,
                FESTIVAL.MENU,
                FESTIVAL.NAME)
                .values(FestivalState.Announce,
                        newTournament.getOpensAt(),
                        Collections.emptyList(),
                        newTournament.getName())
                .returning(FESTIVAL.FID)
                .fetchOne()
                .getFid(), executor);
    }

    public CompletableFuture<Void> setState(Fid fid, FestivalState state) {
        return jooq.update(FESTIVAL)
                .set(FESTIVAL.STATE, state)
                .where(FESTIVAL.FID.eq(fid))
                .executeAsync(executor)
                .thenAccept(n -> {
                    if (n == 0) {
                        throw internalError(
                                "state :state is not set for festival :fid",
                                ImmutableMap.of("state", state, "fid", fid));
                    }
                })
                .toCompletableFuture();
    }

    public CompletableFuture<Integer> setMenu(Fid fid, List<MenuItem> items) {
        return supplyAsync(
                () -> jooq.update(FESTIVAL)
                        .set(FESTIVAL.MENU, items)
                        .where(FESTIVAL.FID.eq(fid))
                        .execute(),
                executor)
                .toCompletableFuture();
    }

    public CompletableFuture<FestivalInfo> getById(Fid fid) {
        return supplyAsync(() ->
                ofNullable(jooq.select()
                        .from(FESTIVAL)
                        .where(FESTIVAL.FID.eq(fid))
                        .fetchOne())
                        .map(r -> FestivalInfo
                                .builder()
                                .fid(fid)
                                .name(r.get(FESTIVAL.NAME))
                                .opensAt(r.get(FESTIVAL.OPENS_AT))
                                .state(r.get(FESTIVAL.STATE))
                                .menu(r.get(FESTIVAL.MENU))
                                .build())
                        .orElseThrow(() -> notFound("Festival not found" , "fid", fid)),
                executor);
    }
}
