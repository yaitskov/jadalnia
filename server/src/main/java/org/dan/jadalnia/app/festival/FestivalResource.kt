package org.dan.jadalnia.app.festival

import org.dan.jadalnia.app.auth.ctx.UserCacheFactory.Companion.USER_SESSIONS
import org.dan.jadalnia.app.festival.ctx.FestivalCacheFactory.Companion.FESTIVAL_CACHE
import org.dan.jadalnia.app.festival.menu.MenuItem
import org.dan.jadalnia.app.festival.pojo.Festival
import org.dan.jadalnia.app.festival.pojo.FestivalState
import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.festival.pojo.NewFestival
import org.dan.jadalnia.app.user.UserInfo
import org.dan.jadalnia.app.user.UserSession
import org.dan.jadalnia.org.dan.jadalnia.app.auth.AuthService.SESSION
import org.dan.jadalnia.sys.async.AsynSync
import org.dan.jadalnia.util.collection.AsyncCache
import org.slf4j.LoggerFactory

import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.HeaderParam
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.container.AsyncResponse
import javax.ws.rs.container.Suspended
import java.util.concurrent.CompletableFuture

import javax.ws.rs.core.MediaType.APPLICATION_JSON

@Path("/")
@Produces(APPLICATION_JSON)
class FestivalResource @Inject constructor(
    val festivalService: FestivalService,
    @Named(USER_SESSIONS)
    val userSessions: AsyncCache<UserSession, UserInfo>,
    @Named(FESTIVAL_CACHE)
    val festivalCache: AsyncCache<Fid, Festival>,
    val asynSync: AsynSync) {
  companion object {
    const val FESTIVAL = "festival/"
    const val FESTIVAL_MENU = FESTIVAL + "menu"
    const val FESTIVAL_STATE = FESTIVAL + "state"
    const val FESTIVAL_CREATE = FESTIVAL + "create"
    const val INVALIDATE_CACHE = FESTIVAL + "invalidate/cache"
    val log = LoggerFactory.getLogger(FestivalResource::class.java)
  }

  @POST
  @Path(FESTIVAL_CREATE)
  @Consumes(APPLICATION_JSON)
  fun create(
      @Suspended response: AsyncResponse,
      newFestival: NewFestival) {
    asynSync.sync(festivalService.create(newFestival), response)
  }

  @POST
  @Path(FESTIVAL_STATE)
  @Consumes(APPLICATION_JSON)
  fun setState(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession,
      state: FestivalState) {
    asynSync.sync(
        userSessions.get(session)
            .thenApply({ user -> user.ensureAdmin().fid })
            .thenCompose(festivalCache::get)
            .thenCompose({ festival ->
              festivalService.setState(festival, state)
            }),
        response)
  }

  @GET
  @Path(FESTIVAL_STATE + "/{fid}")
  fun getState(
      @Suspended response: AsyncResponse,
      @PathParam("fid") fid: Fid) {
    asynSync.sync(festivalService.getState(fid), response)
  }

  @GET
  @Path(FESTIVAL_MENU + "/{fid}")
  fun listMenu(
      @Suspended response: AsyncResponse,
      @PathParam("fid") fid: Fid) {
    asynSync.sync(festivalService.listMenu(fid), response)
  }

  @POST
  @Path(FESTIVAL_MENU)
  fun updateMenu(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession,
      items: List<MenuItem>) {
    asynSync.sync(
        userSessions.get(session)
            .thenApply({ user -> user.ensureAdmin().fid })
            .thenCompose(festivalCache::get)
            .thenCompose({ festival ->
              festivalService.updateMenu(festival, items)
            }),
        response)
  }

  @POST
  @Path(INVALIDATE_CACHE)
  @Consumes(APPLICATION_JSON)
  fun invalidateCache(
      @HeaderParam(SESSION) session: UserSession)
      : CompletableFuture<Void> {
    return userSessions.get(session)
        .thenApply({ user -> user.ensureAdmin().fid })
        .thenAccept({ fid -> festivalCache.invalidate(fid) })
  }
}
