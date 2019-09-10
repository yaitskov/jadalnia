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
import org.dan.jadalnia.sys.ctx.FutureExecutor;
import org.jooq.DSLContext;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.Optional.ofNullable;
import static org.dan.jadalnia.jooq.Tables.FESTIVAL;
import static org.dan.jadalnia.sys.error.JadEx.internalError;
import static org.dan.jadalnia.sys.error.JadEx.notFound;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FestivalDao {
    DSLContext jooq;
    FutureExecutor executor;

    public CompletableFuture<Fid> create(NewFestival newTournament) {
        return executor.run(() -> jooq.insertInto(FESTIVAL,
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
                .getFid());
    }

    public CompletableFuture<Void> setState(Fid fid, FestivalState state) {
        return jooq.update(FESTIVAL)
                .set(FESTIVAL.STATE, state)
                .where(FESTIVAL.FID.eq(fid))
                .executeAsync(executor.getExecutorService())
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
        return executor.run(
                () -> jooq.update(FESTIVAL)
                        .set(FESTIVAL.MENU, items)
                        .where(FESTIVAL.FID.eq(fid))
                        .execute())
                .toCompletableFuture();
    }

    public CompletableFuture<FestivalInfo> getById(Fid fid) {
        return executor.run(() ->
                ofNullable(jooq.select()
                        .from(FESTIVAL)
                        .where(FESTIVAL.FID.eq(fid))
                        .fetchOne())
                        .map(r -> new FestivalInfo(
                                        fid,
                                        r.get(FESTIVAL.NAME),
                                        r.get(FESTIVAL.STATE),
                                        r.get(FESTIVAL.MENU),
                                        r.get(FESTIVAL.OPENS_AT)))
                        .orElseThrow(() -> notFound("Festival not found" , "fid", fid)));
    }
}
