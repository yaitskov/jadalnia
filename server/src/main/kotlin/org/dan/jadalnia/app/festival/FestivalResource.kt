package org.dan.jadalnia.app.festival

import org.dan.jadalnia.app.festival.ctx.FestivalCacheFactory.Companion.FESTIVAL_CACHE
import org.dan.jadalnia.app.festival.menu.MenuItem
import org.dan.jadalnia.app.festival.pojo.FestParams
import org.dan.jadalnia.app.festival.pojo.Festival
import org.dan.jadalnia.app.festival.pojo.FestivalState
import org.dan.jadalnia.app.festival.pojo.FestivalVolunteerInfo
import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.festival.pojo.NewFestival
import org.dan.jadalnia.app.user.UserSession
import org.dan.jadalnia.app.user.WithUser
import org.dan.jadalnia.org.dan.jadalnia.app.auth.AuthService.SESSION
import org.dan.jadalnia.sys.async.AsynSync
import org.dan.jadalnia.util.collection.AsyncCache
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture.completedFuture
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
import javax.ws.rs.core.MediaType.APPLICATION_JSON

@Path("/")
@Produces(APPLICATION_JSON)
class FestivalResource @Inject constructor(
    val withUser: WithUser,
    private val festivalService: FestivalService,
    @Named(FESTIVAL_CACHE)
    val festivalCache: AsyncCache<Fid, Festival>) {
  companion object {
    const val FESTIVAL = "festival/"
    const val FESTIVAL_MENU = FESTIVAL + "menu"
    const val FESTIVAL_STATE = FESTIVAL + "state"
    const val FESTIVAL_CREATE = FESTIVAL + "create"
    const val FESTIVAL_VOLUNTEER_INFO = FESTIVAL + "volunteer-info"
    const val INVALIDATE_CACHE = FESTIVAL + "invalidate/cache"
    val log = LoggerFactory.getLogger(FestivalResource::class.java)
  }

  @POST
  @Path(FESTIVAL_CREATE)
  @Consumes(APPLICATION_JSON)
  fun create(
      @Suspended response: AsyncResponse,
      newFestival: NewFestival) {
    withUser.anonymous(response, festivalService.create(newFestival))
  }

  @POST
  @Path("$FESTIVAL_STATE/fid/{state}")
  @Consumes(APPLICATION_JSON)
  fun setState(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession,
      @PathParam("state") state: FestivalState)
      = withUser.adminFest(response, session) { festival ->
    festivalService.setState(festival, state)
  }

  @GET
  @Path("$FESTIVAL_VOLUNTEER_INFO/{fid}")
  fun getInfoForVolunteer(
      @Suspended response: AsyncResponse,
      @PathParam("fid") fid: Fid) = withUser.withFest(fid, response) { fest ->
        val info = fest.info.get()
        completedFuture(FestivalVolunteerInfo(
            name = info.name,
            state = info.state,
            opensAt = info.opensAt))
      }

  @GET
  @Path("$FESTIVAL_STATE/{fid}")
  fun getState(
      @Suspended response: AsyncResponse,
      @PathParam("fid") fid: Fid)
      = withUser.anonymous(response, festivalService.getState(fid))

  @GET
  @Path("$FESTIVAL_MENU/{fid}")
  fun listMenu(
      @Suspended response: AsyncResponse,
      @PathParam("fid") fid: Fid)
      = withUser.anonymous(response, festivalService.listMenu(fid))

  @POST
  @Path(FESTIVAL_MENU)
  fun updateMenu(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession,
      items: List<MenuItem>) = withUser.adminFest(response, session) { festival ->
    festivalService.updateMenu(festival, items)
  }

  @POST
  @Path("${FESTIVAL}params")
  fun updateParams(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession,
      params: FestParams) = withUser.adminFest(response, session) { fest ->
    festivalService.updateParams(fest, params)
  }

  @GET
  @Path("${FESTIVAL}params/{fid}")
  fun getParams(
      @Suspended response: AsyncResponse,
      @PathParam("fid") fid: Fid)
      = withUser.withFest(fid, response, festivalService::getParams)

  @POST
  @Path(INVALIDATE_CACHE)
  @Consumes(APPLICATION_JSON)
  fun invalidateCache(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession)
      = withUser.adminFest(response, session) { festival ->
    completedFuture(festivalCache.invalidate(festival.fid()))
  }
}
