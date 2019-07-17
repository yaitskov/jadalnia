package org.dan.jadalnia.app.festival;

import static java.util.Optional.ofNullable;
import static org.dan.jadalnia.jooq.Tables.FESTIVAL;
import static org.dan.jadalnia.sys.db.DbContext.TRANSACTION_MANAGER;

import com.github.jasync.sql.db.Connection;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.dan.jadalnia.sys.validation.FidBodyRequired;
import org.jooq.DSLContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FestivalDao {
    private static final String ENLISTED = "enlisted";
    private static final String PARTICIPANTS = "participants";
    private static final String GAMES = "games";
    private static final String GAMES_COMPLETE = "gamesComplete";
    private static final int DAYS_TO_SHOW_PAST_TOURNAMENT = 30;
    private static final String CATEGORIES = "categories";

    DSLContext jooq;
    Connection connection;

    @Transactional(TRANSACTION_MANAGER)
    public Fid create(NewFestival newTournament) {
        return jooq.insertInto(FESTIVAL,
                FESTIVAL.STATE,
                FESTIVAL.OPENS_AT,
                FESTIVAL.MENU,
                FESTIVAL.NAME)
                .values(FestivalState.Announce,
                        newTournament.getOpensAt(),
                        Collections.emptyList(),
                        newTournament.getName())
                .returning(FESTIVAL.TID)
                .fetchOne()
                .getTid();
    }

    @Transactional(TRANSACTION_MANAGER)
    public void setState(Fid fid, FestivalState state) {
        jooq.update(FESTIVAL)
                .set(FESTIVAL.STATE, state)
                .where(FESTIVAL.FID.eq(fid))
                .execute();
    }

    @Transactional(readOnly = true, transactionManager = TRANSACTION_MANAGER)
    public List<MenuItem> getMenu(Fid fid) {
        return ofNullable(jooq
                .select(FESTIVAL.MENU)
                .from(FESTIVAL)
                .where(FESTIVAL.FID.eq(fid))
                .getSQL()
                .fetchOne())
                .map(r -> r.get(FESTIVAL.MENU))
                .orElseGet(Collections::emptyList);
    }

    public void setMenu(Fid fid, List<MenuItem> items) {
        jooq.update(FESTIVAL)
                .set(FESTIVAL.MENU, items)
                .where(FESTIVAL.FID.eq(fid))
                .execute();
    }

    public CompletableFuture<> getById(Fid fid) {
        jooq.fetch()
        return connection.sendPreparedStatement(

        );
    }
}
