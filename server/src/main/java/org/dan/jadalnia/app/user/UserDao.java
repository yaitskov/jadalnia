package org.dan.jadalnia.app.user;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.dan.jadalnia.app.festival.pojo.Fid;
import org.jooq.DSLContext;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static java.util.Optional.ofNullable;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.dan.jadalnia.jooq.Tables.USERS;

@Slf4j
@FieldDefaults(makeFinal = true)
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class UserDao {
    DSLContext jooq;
    ExecutorService executorService;;

    public CompletableFuture<UserSession> register(
            Fid fid, UserType userType, UserState userState,
            String userName, String sessionKey) {
        return supplyAsync(() ->
                        UserSession.builder()
                                .key(sessionKey)
                                .uid(jooq
                                        .insertInto(USERS,
                                                USERS.FESTIVAL_ID,
                                                USERS.TYPE,
                                                USERS.STATE,
                                                USERS.NAME,
                                                USERS.SESSION_KEY)
                                        .values(fid, userType, userState,
                                                userName, sessionKey)
                                        .returning(USERS.UID)
                                        .fetchOne()
                                        .getValue(USERS.UID))
                                .build(),
                executorService);
    }

    public CompletableFuture<UserInfo> getUserBySession(UserSession userSession) {
        return supplyAsync(() -> ofNullable(jooq
                .select(USERS.UID, USERS.NAME,
                        USERS.STATE, USERS.TYPE, USERS.FESTIVAL_ID)
                .from(USERS)
                .where(USERS.UID.eq(userSession.getUid()),
                        USERS.SESSION_KEY.eq(userSession.getKey()))
                .fetchOne())
                .map(r -> UserInfo.builder()
                        .uid(r.getValue(USERS.UID))
                        .name(r.getValue(USERS.NAME))
                        .userState(r.getValue(USERS.STATE))
                        .userType(r.getValue(USERS.TYPE))
                        // .festivalId(r.getValue(USERS.FESTIVAL_ID))
                        .build())
                .orElseThrow(() -> new NotFoundException("user is not known")));
    }
}
