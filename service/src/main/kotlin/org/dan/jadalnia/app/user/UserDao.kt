package org.dan.jadalnia.app.user

import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.label.AsyncDao
import java.util.concurrent.CompletableFuture

import java.util.Optional.ofNullable
import org.dan.jadalnia.jooq.Tables.USERS
import org.dan.jadalnia.sys.error.JadEx.Companion.notFound

class UserDao : AsyncDao() {
  fun register(
      fid: Fid, userType: UserType, userState: UserState,
      userName: String, sessionKey: String)
      :
      CompletableFuture<UserSession> {
    return execQuery { jooq ->
      UserSession(
          key = sessionKey,
          uid = jooq.insertInto(USERS,
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
    }
  }

  fun getUserBySession(userSession: UserSession)
      : CompletableFuture<UserInfo> {
    return execQuery { jooq ->
      ofNullable(
          jooq
              .select(USERS.UID, USERS.NAME,
                  USERS.STATE, USERS.TYPE, USERS.FESTIVAL_ID)
              .from(USERS)
              .where(USERS.UID.eq(userSession.uid),
                  USERS.SESSION_KEY.eq(userSession.key))
              .fetchOne())
          .map { r ->
            UserInfo(
                uid = r.getValue(USERS.UID),
                name = r.getValue(USERS.NAME),
                userState = r.getValue(USERS.STATE),
                userType = r.getValue(USERS.TYPE),
                fid = r.getValue(USERS.FESTIVAL_ID))
          }
          .orElseThrow {
            notFound("session is not valid", "session", "" + userSession)
          }
    }
  }

  fun listByType(fid: Fid, userType: UserType) = execQuery {
    jooq -> jooq
      .select(USERS.UID, USERS.NAME)
      .from(USERS)
      .where(USERS.FESTIVAL_ID.eq(fid), USERS.TYPE.eq(userType))
      .fetch().map { r -> UserLink(
          uid = r.get(USERS.UID),
          name = r.get(USERS.NAME))
      }
  }
}
