package org.dan.jadalnia.app.order

import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.order.pojo.MarkOrderPaid
import org.dan.jadalnia.app.order.pojo.OrderItem
import org.dan.jadalnia.app.order.pojo.OrderLabel
import org.dan.jadalnia.app.user.Uid
import org.dan.jadalnia.app.user.UserSession
import org.dan.jadalnia.app.user.WithUser
import org.dan.jadalnia.org.dan.jadalnia.app.auth.AuthService.SESSION
import org.slf4j.LoggerFactory
import javax.inject.Inject
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
    val with: WithUser, val orderService: OrderService) {
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
    with.kasierFest(response, session) { festival ->
      orderService.markOrderPaid(festival, paidOrder)
    }
  }

  @POST
  @Path(PICKUP_ORDER)
  fun customerPicksUpOrder(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession,
      label: OrderLabel) {
    with.customerFest(response, session) { festival ->
      orderService.pickUpReadyOrder(festival, session.uid, label)
    }
  }

  @GET
  @Path(MY_ORDERS)
  fun listMyOrders(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession) {
    with.customer(response, session) {
      fid -> orderService.listCustomerOrders(fid, session.uid)
    }
  }

  @GET
  @Path(GET_ORDER + "/{label}")
  fun showOrderToKelner(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession,
      @PathParam("label")
      label: OrderLabel) {
    with.kelner(response, session) { fid -> orderService.showOrderToKelner(fid, label) }
  }

  @GET
  @Path(ORDER + "customerInfo/{label}")
  fun showOrderToVisitor(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession,
      @PathParam("label")
      label: OrderLabel) {
    with.customer(response, session) { fid -> orderService.showOrderToVisitor(fid, label) }
  }

  @GET
  @Path(ORDER + "progress/{fid}/{label}")
  fun showOrderProgressToVisitor(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession,
      @PathParam("fid")
      fid: Fid,
      @PathParam("label")
      label: OrderLabel) {
    with.anonymous(response, orderService.showOrderProgressToVisitor(fid, label))
  }

  @GET
  @Path(EXECUTING_ORDER)
  fun getKelnerExecutingOrder(@Suspended response: AsyncResponse,
                              @HeaderParam(SESSION) session: UserSession) {
    with.kelnerFest(response, session) { festival ->
      orderService.kelnerTakenOrderId(festival, session.uid)
    }
  }

  @POST
  @Path(TRY_ORDER)
  fun tryToExecOrder(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession) {
    with.kelnerFest(response, session) { festival ->
      log.info("Kelner {} in fest {} wants to pick up an order to exec",
          session.uid, festival.fid())
      orderService.tryToExecOrder(festival, session.uid)
          .thenApply { label ->
            log.info("Kelner {} in fest {} took order {}",
                session.uid, festival.fid(), label)
            label
          }
    }
  }

  @GET
  @Path(COUNT_ORDERS_READY_FOR_EXEC)
  fun countOrdersReadyForExec(@Suspended response: AsyncResponse,
                              @HeaderParam(SESSION) session: UserSession) {
    with.kelnerFest(response, session, orderService::countReadyForExec)
  }

  @POST
  @Path("${ORDER}customer-missing/{label}")
  fun customerDidNotShowUpToStartOrderExecution(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession,
      @PathParam("label") label: OrderLabel) {
    with.kelnerFest(response, session) { festival ->
      log.info("Kelner {} in {} said order {} is abandoned",
          session.uid, festival.fid(), label)
      orderService.customerDidNotShowUpToStartOrderExecution(
          festival, session.uid, label)
    }
  }

  @POST
  @Path("${ORDER}kelner-tired/{label}")
  fun kelnerWithAssignedOrderResigns(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession,
      @PathParam("label") label: OrderLabel) {
    with.kelnerFest(response, session) { festival ->
      log.info("Kelner {} in {} tried while preparing order {}",
          session.uid, festival.fid(), label)
      orderService.kelnerWithAssignedOrderResigns(
          festival, session.uid, label)
    }
  }

  @POST
  @Path("$ORDER_READY/{label}")
  fun markOrderReadyToPickup(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession,
      @PathParam("label") label: OrderLabel) {
    with.kelnerFest(response, session) { festival ->
      log.info("Kelner {} in {} complete order {}", session.uid, festival.fid(), label)
      orderService.markOrderReadyToPickup(festival, session.uid, label)
    }
  }

  @POST
  @Path(PUT_ORDER)
  fun create(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession,
      newOrderItems: List<OrderItem>) {
    with.customerFest(response, session) { festival ->
      log.info("Customer {} in {} puts order {}", session.uid, festival.fid(), newOrderItems)
      orderService.putNewOrder(festival, session, newOrderItems)
          .thenApply {label ->
            log.info("New order {} is put by {} in {}", label, session.uid, festival.fid())
            label
          }
    }
  }

  @POST
  @Path(PAY_ORDER + "/{label}")
  fun customerPaysForOrder(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession,
      @PathParam("label") orderLabel: OrderLabel) {
    log.info("Customer {} try to pay order {}", session.uid, orderLabel)
    with.customerFest(response, session) { festival ->
      orderService.customerPays(festival, session.uid, orderLabel)
    }
  }

  @POST
  @Path(KASIER_PAY_ORDER)
  fun kasierPaysCustomerOrders(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) kasierSession: UserSession,
      customerUid: Uid) {
    log.info("Kasier {} try to pay orders for {}",
        kasierSession.uid, customerUid)
    with.kasierFest(response, kasierSession) { festival ->
      orderService.kasierPaysCustomerOrders(festival, customerUid)
    }
  }

  @POST
  @Path("${ORDER}reschedule/{label}")
  fun customerReschedulesAbandonedOrder(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession,
      @PathParam("label") orderLabel: OrderLabel) {
    with.customerFest(response, session) { festival ->
      log.info("Customer {} reschedules abandoned order {} in {}",
          session.uid, orderLabel, festival.fid())
      orderService.customerReschedules(festival, orderLabel)
    }
  }
}
