package org.dan.jadalnia.app.user;

import static java.util.Optional.ofNullable;
import static org.dan.jadalnia.app.user.UserType.Admin;
import static org.dan.jadalnia.jooq.Tables.ADMIN;
import static org.dan.jadalnia.jooq.Tables.USERS;
import static org.dan.jadalnia.sys.db.DbContext.TRANSACTION_MANAGER;
import static org.dan.jadalnia.sys.error.JadalniaEx.badRequest;

import lombok.extern.slf4j.Slf4j;
import org.dan.jadalnia.app.bid.Uid;
import org.dan.jadalnia.app.festival.Fid;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import javax.inject.Inject;

@Slf4j
public class UserDao {
    @Inject
    private DSLContext jooq;

    @Transactional(transactionManager = TRANSACTION_MANAGER)
    public UserSession register(Fid fid, UserType userType,
            UserState userState, String userName, String userKey) {
        final Uid uid = jooq
                .insertInto(USERS,
                        USERS.FESTIVAL_ID,
                        USERS.TYPE,
                        USERS.STATE,
                        USERS.NAME,
                        USERS.SESSION_KEY)
                .values(fid, userType, userState,
                        userName, userKey)
                .returning(USERS.UID)
                .fetchOne()
                .getValue(USERS.UID);
        return UserSession.builder()
                .key(userKey)
                .uid(uid)
                .build();
    }

    @Transactional(value = TRANSACTION_MANAGER, readOnly = true)
    public Optional<UserInfo> getUserBySession(UserSession userSession) {
        return ofNullable(jooq
                .select(USERS.UID, USERS.NAME,
                        USERS.STATE, USERS.TYPE, USERS.FESTIVAL_ID)
                .from(USERS)
                .where(USERS.UID.eq(userSession.getUid())
                        USERS.SESSION_KEY.eq(userSession.getKey()))
                .fetchOne())
                .map(r -> UserInfo.builder().uid(r.getValue(USERS.UID))
                        .name(r.getValue(USERS.NAME))
                        .userState(r.getValue(USERS.STATE))
                        .userType(r.getValue(USERS.TYPE))
                        .festivalId(r.getValue(USERS.FESTIVAL_ID))
                        .build());
    }

    @Transactional(TRANSACTION_MANAGER)
    public void promoteToAdmins(int said, Uid uid) {
        log.info("Sys admin {} promoted user {} to admins", said, uid);
        jooq.insertInto(ADMIN, ADMIN.UID, ADMIN.SAID)
                .values(uid, said)
                .onDuplicateKeyIgnore()
                .execute();

        jooq.update(USERS)
                .set(USERS.TYPE, Admin)
                .where(USERS.UID.eq(uid)).execute();
    }

    @Transactional(TRANSACTION_MANAGER)
    public void requestAdminAccess(Uid uid, Instant now) {
        jooq.update(USERS)
                .set(USERS.WANT_ADMIN, Optional.of(now))
                .where(USERS.UID.eq(uid))
                .execute();
    }

    @Transactional(TRANSACTION_MANAGER)
    public void update(UserInfo userInfo, UserProfileUpdate update) {
        jooq.update(USERS)
                .set(USERS.EMAIL, update.getEmail())
                .set(USERS.PHONE, update.getPhone())
                .set(USERS.NAME, update.getName())
                .where(USERS.UID.eq(userInfo.getUid()))
                .execute();
        if (!update.getEmail().equals(userInfo.getEmail())) {
            validateEmailUnique(update.getEmail());
            log.info("User {} changed email from {} to {}",
                    userInfo.getUid(), userInfo.getEmail(), update.getEmail());
        }
    }

    @Transactional(TRANSACTION_MANAGER)
    public Uid registerOffline(Instant now, OfflineUserRegRequest regRequest, Uid adminUid) {
        validateRegOfflineLimits(now, adminUid, 1);
        return registerOfflineNoValidation(regRequest, adminUid);
    }

    @Transactional(TRANSACTION_MANAGER)
    public Uid registerOfflineNoValidation(OfflineUserRegRequest regRequest, Uid adminUid) {
        return jooq
                .insertInto(USERS, USERS.NAME, USERS.TYPE, USERS.REF_UID)
                .values(regRequest.getName(), UserType.OfUsr, adminUid)
                .returning(USERS.UID)
                .fetchOne()
                .getValue(USERS.UID);
    }

    public void validateRegOfflineLimits(Instant now, Uid adminUid, int requested) {
        final int lastDay = count(adminUid, now.minus(1, ChronoUnit.DAYS))
                .fetchOne().value1();
        if (lastDay + requested > 100) {
            throw badRequest("Too many offline users has been registered");
        }
        final int lastWeek = count(adminUid, now.minus(7, ChronoUnit.DAYS))
                .fetchOne().value1();
        if (lastWeek + requested > 300) {
            throw badRequest("Too many offline users has been registered");
        }
        final int lastMonth = count(adminUid, now.minus(31, ChronoUnit.DAYS))
                .fetchOne().value1();
        if (lastMonth + requested > 1000) {
            throw badRequest("Too many offline users has been registered");
        }
    }

    public SelectConditionStep<Record1<Integer>> count(Uid adminUid, Instant oneDay) {
        return jooq.select(USERS.UID.count())
                .from(USERS)
                .where(USERS.REF_UID.eq(adminUid),
                        USERS.CREATED.ge(oneDay));
    }
}
