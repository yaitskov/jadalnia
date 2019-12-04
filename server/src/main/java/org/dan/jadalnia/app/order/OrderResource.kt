package org.dan.jadalnia.app.order

import org.dan.jadalnia.app.auth.ctx.UserCacheFactory.Companion.USER_SESSIONS
import org.dan.jadalnia.app.festival.ctx.FestivalCacheFactory.Companion.FESTIVAL_CACHE
import org.dan.jadalnia.app.festival.pojo.Festival
import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.order.pojo.OrderItem
import org.dan.jadalnia.app.order.pojo.MarkOrderPaid
import org.dan.jadalnia.app.order.pojo.OrderLabel
import org.dan.jadalnia.app.user.Uid
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
    const val MY_ORDERS = ORDER + "listMine"
    const val COUNT_ORDERS_READY_FOR_EXEC = ORDER + "count-ready-for-exec"
    const val EXECUTING_ORDER = ORDER + "executing"
    const val TRY_ORDER = ORDER + "try"
    const val ORDER_READY = ORDER + "ready"
    const val PICKUP_ORDER = ORDER + "pickup"
    const val PAY_ORDER = ORDER + "pay"
    const val KASIER_PAY_ORDER = ORDER + "kasierPay"
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

  @POST
  @Path(PICKUP_ORDER)
  fun customerPicksUpOrder(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession,
      label: OrderLabel) {
    asynSync.sync(
        userSessions.get(session)
            .thenApply { user -> user.ensureCustomer().fid }
            .thenCompose(festivalCache::get)
            .thenCompose { festival ->
              orderService.pickUpReadyOrder(festival, session.uid, label)
            },
        response)
  }

  @GET
  @Path(MY_ORDERS)
  fun listMyOrders(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession) {
    asynSync.sync(
        userSessions.get(session)
            .thenApply { user -> user.ensureCustomer().fid }
            .thenCompose { fid -> orderService.listCustomerOrders(fid, session.uid) },
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

  @GET
  @Path(ORDER + "customerInfo/{label}")
  fun showOrderToVisitor(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession,
      @PathParam("label")
      label: OrderLabel) {
    asynSync.sync(
        userSessions.get(session)
            .thenApply { user -> user.ensureCustomer().fid }
            .thenCompose { fid -> orderService.showOrderToVisitor(fid, label) },
        response)
  }

  @GET
  @Path(ORDER + "progress/{fid}/{label}")
  fun showOrderProgressToVisitor(
      @Suspended response: AsyncResponse,
      @PathParam("fid")
      fid: Fid,
      @PathParam("label")
      label: OrderLabel) {
    asynSync.sync(
        orderService.showOrderProgressToVisitor(fid, label),
        response)
  }

  @GET
  @Path(EXECUTING_ORDER)
  fun getKelnerExecutingOrder(@Suspended response: AsyncResponse,
                              @HeaderParam(SESSION) session: UserSession) {
    asynSync.sync(
        userSessions.get(session)
            .thenApply { user -> user.ensureKelner().fid }
            .thenCompose(festivalCache::get)
            .thenCompose { festival ->
              orderService.kelnerTakenOrderId(festival, session.uid)
            },
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

  @GET
  @Path(COUNT_ORDERS_READY_FOR_EXEC)
  fun countOrdersReadyForExec(@Suspended response: AsyncResponse,
                              @HeaderParam(SESSION) session: UserSession) {
    asynSync.sync(
        userSessions.get(session)
            .thenApply { user -> user.ensureKelner().fid }
            .thenCompose(festivalCache::get)
            .thenCompose(orderService::countReadyForExec),
        response)
  }

  @POST
  @Path(ORDER_READY)
  fun markOrderReadyToPickup(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession,
      label: OrderLabel) {
    asynSync.sync(
        userSessions.get(session)
            .thenApply { user -> user.ensureKelner().fid }
            .thenCompose(festivalCache::get)
            .thenCompose { festival ->
              orderService.markOrderReadyToPickup(festival, session.uid, label)
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

  @POST
  @Path(PAY_ORDER + "/{label}")
  fun customerPaysForOrder(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession,
      @PathParam("label") orderLabel: OrderLabel) {
    log.info("Customer {} try to pay order {}", session.uid, orderLabel)
    asynSync.sync(
        userSessions.get(session)
            .thenApply { user -> user.ensureCustomer().fid }
            .thenCompose(festivalCache::get)
            .thenCompose { festival ->
              orderService.customerPays(festival, session.uid, orderLabel)
            },
        response)
  }

  @POST
  @Path(KASIER_PAY_ORDER)
  fun kasierPaysCustomerOrders(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) kasierSession: UserSession,
      customerUid: Uid) {
    log.info("Kasier {} try to pay orders for {}",
        kasierSession.uid, customerUid)
    asynSync.sync(
        userSessions.get(kasierSession)
            .thenApply { user -> user.ensureKasier().fid }
            .thenCompose(festivalCache::get)
            .thenCompose { festival ->
              orderService.kasierPaysCustomerOrders(festival, customerUid)
            },
        response)
  }

//  @POST
//  @Path(UNPAID_ORDER)
//  fun howMuchCustomerHasToPayForOrders(
//      @Suspended response: AsyncResponse,
//      @HeaderParam(SESSION) session: UserSession,
//      orderLabel: OrderLabel) {
//    asynSync.sync(
//        userSessions.get(session)
//            .thenApply { user -> user.ensureCustomer().fid }
//            .thenCompose(festivalCache::get)
//            .thenCompose { festival ->
//              orderService.customerPays(
//                  festival, session, orderLabel)
//            },
//        response)
//  }

}
