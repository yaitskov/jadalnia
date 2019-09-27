package org.dan.jadalnia.app.order

import org.dan.jadalnia.app.auth.ctx.UserCacheFactory.Companion.USER_SESSIONS
import org.dan.jadalnia.app.festival.ctx.FestivalCacheFactory.Companion.FESTIVAL_CACHE
import org.dan.jadalnia.app.festival.pojo.Festival
import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.order.pojo.OrderItem
import org.dan.jadalnia.app.order.pojo.MarkOrderPaid
import org.dan.jadalnia.app.order.pojo.OrderLabel
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

import javax.ws.rs.core.MediaType.APPLICATION_JSON

@Path("/")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
class OrderResource @Inject constructor(
    val orderService: OrderService,
    @Named(USER_SESSIONS)
    val userSessions: AsyncCache<UserSession, UserInfo>,
    @Named(FESTIVAL_CACHE)
    val festivalCache: AsyncCache<Fid, Festival>,
    val asynSync: AsynSync) {
  companion object {
    const val ORDER = "order/"
    const val PUT_ORDER = ORDER + "put"
    const val ORDER_PAID = ORDER + "paid"
    const val GET_ORDER = ORDER + "get"
    const val TRY_ORDER = ORDER + "try"
    const val ORDER_READY = ORDER + "ready"
    val log = LoggerFactory.getLogger(OrderService::class.java)
  }

  @POST
  @Path(ORDER_PAID)
  fun markOrderPaid(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession,
      paidOrder: MarkOrderPaid) {
    asynSync.sync(
        userSessions.get(session)
            .thenApply { user -> user.ensureKasier().fid }
            .thenCompose(festivalCache::get)
            .thenCompose { festival ->
              orderService.markOrderPaid(festival, paidOrder)
            },
        response)
  }

  @GET
  @Path(GET_ORDER + "/{label}")
  fun showOrderToKelner(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession,
      @PathParam("label")
      label: OrderLabel) {
    asynSync.sync(
        userSessions.get(session)
            .thenApply { user -> user.ensureKelner().fid }
            .thenCompose { fid -> orderService.showOrderToKelner(fid, label) },
        response)
  }

  @POST
  @Path(TRY_ORDER)
  fun tryToExecOrder(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession) {
    asynSync.sync(
        userSessions.get(session)
            .thenApply { user -> user.ensureKelner().fid }
            .thenCompose(festivalCache::get)
            .thenCompose { festival ->
              orderService.tryToExecOrder(festival, session.uid)
            },
        response)
  }

  @POST
  @Path(PUT_ORDER)
  fun create(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession,
      newOrderItems: List<OrderItem>) {
    asynSync.sync(
        userSessions.get(session)
            .thenApply { user -> user.ensureCustomer().fid }
            .thenCompose(festivalCache::get)
            .thenCompose { festival ->
              orderService.putNewOrder(
                  festival, session, newOrderItems)
            },
        response)
  }
}
