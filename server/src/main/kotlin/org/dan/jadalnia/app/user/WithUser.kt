package org.dan.jadalnia.app.user

import org.dan.jadalnia.app.auth.ctx.UserCacheFactory.Companion.USER_SESSIONS
import org.dan.jadalnia.app.festival.ctx.FestivalCacheFactory.Companion.FESTIVAL_CACHE
import org.dan.jadalnia.app.festival.pojo.Festival
import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.sys.async.AsynSync
import org.dan.jadalnia.util.collection.AsyncCache
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.completedFuture
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.container.AsyncResponse

class WithUser @Inject constructor (
    @Named(USER_SESSIONS)
    val userSessions: AsyncCache<UserSession, UserInfo>,
    @Named(FESTIVAL_CACHE)
    val festivalCache: AsyncCache<Fid, Festival>,
    val asynSync: AsynSync) {

  fun <T> anonymous(
      response: AsyncResponse,
      action: CompletableFuture<T>) {
    asynSync.sync(action, response)
  }

   fun <TT> withFest(fid: Fid, response: AsyncResponse,
                     f: Function1<Festival, CompletableFuture<TT>>) {
     anonymous(response, festivalCache.get(fid).thenCompose(f))
  }

  private fun <T, I> withUser(
      check: Function1<UserInfo, CompletableFuture<I>>,
      response: AsyncResponse,
      session: UserSession,
      action: Function1<I, CompletableFuture<T>>) {
    anonymous(
        response,
        userSessions.get(session)
            .thenCompose(check)
            .thenCompose(action::invoke))
  }

  fun <T> withUserFest(
      check: Function1<UserInfo, UserInfo>,
      response: AsyncResponse,
      session: UserSession,
      action: Function1<Festival, CompletableFuture<T>>)
      = withUser({ user -> festivalCache.get((check(user).fid)) },
      response, session, action)

  fun <T> customerFest(
      response: AsyncResponse,
      session: UserSession,
      action: Function1<Festival, CompletableFuture<T>>)
      = withUserFest(UserInfo::ensureCustomer, response, session, action)

  fun <T> kasierFest(
      response: AsyncResponse,
      session: UserSession,
      action: Function1<Festival, CompletableFuture<T>>)
      = withUserFest(UserInfo::ensureKasier, response, session, action)

  fun <T> kelnerFest(
      response: AsyncResponse,
      session: UserSession,
      action: Function1<Festival, CompletableFuture<T>>)
      = withUserFest(UserInfo::ensureKelner, response, session, action)

  fun <T> adminFest(
      response: AsyncResponse,
      session: UserSession,
      action: Function1<Festival, CompletableFuture<T>>)
      = withUserFest(UserInfo::ensureAdmin, response, session, action)

  fun <T> customer(
      response: AsyncResponse,
      session: UserSession,
      action: Function1<Fid, CompletableFuture<T>>) {
    withUser(
        { user -> completedFuture((user.ensureCustomer().fid)) },
        response, session, action)
  }

  fun <T> kelner(
      response: AsyncResponse,
      session: UserSession,
      action: Function1<Fid, CompletableFuture<T>>) {
    withUser(
        { user -> completedFuture((user.ensureKelner().fid)) },
        response, session, action)
  }
}