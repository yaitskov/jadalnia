package org.dan.jadalnia.app.order

import org.dan.jadalnia.app.festival.ctx.FestivalCacheFactory.Companion.FESTIVAL_CACHE
import org.dan.jadalnia.app.festival.menu.DishName
import org.dan.jadalnia.app.festival.pojo.Festival
import org.dan.jadalnia.app.festival.pojo.FestivalState
import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.label.LabelService
import org.dan.jadalnia.app.order.PaymentAttemptOutcome.ALREADY_PAID
import org.dan.jadalnia.app.order.PaymentAttemptOutcome.CANCELLED
import org.dan.jadalnia.app.order.PaymentAttemptOutcome.FESTIVAL_OVER
import org.dan.jadalnia.app.order.PaymentAttemptOutcome.NOT_ENOUGH_FUNDS
import org.dan.jadalnia.app.order.PaymentAttemptOutcome.ORDER_PAID
import org.dan.jadalnia.app.order.PaymentAttemptOutcome.RETRY
import org.dan.jadalnia.app.order.complete.CustomerAbsent
import org.dan.jadalnia.app.order.complete.KelnerResigns
import org.dan.jadalnia.app.order.complete.LowFood
import org.dan.jadalnia.app.order.complete.OrderReady
import org.dan.jadalnia.app.order.pojo.MarkOrderPaid
import org.dan.jadalnia.app.order.pojo.OrderItem
import org.dan.jadalnia.app.order.pojo.OrderLabel
import org.dan.jadalnia.app.order.pojo.OrderMem
import org.dan.jadalnia.app.order.pojo.OrderState
import org.dan.jadalnia.app.order.pojo.OrderState.Abandoned
import org.dan.jadalnia.app.order.pojo.OrderState.Accepted
import org.dan.jadalnia.app.order.pojo.OrderState.Cancelled
import org.dan.jadalnia.app.order.pojo.OrderState.Delayed
import org.dan.jadalnia.app.order.pojo.OrderState.Executing
import org.dan.jadalnia.app.order.pojo.OrderState.Handed
import org.dan.jadalnia.app.order.pojo.OrderState.Paid
import org.dan.jadalnia.app.order.pojo.OrderState.Ready
import org.dan.jadalnia.app.order.pojo.ProblemOrder
import org.dan.jadalnia.app.token.TokenBalance
import org.dan.jadalnia.app.token.TokenPoints
import org.dan.jadalnia.app.user.Uid
import org.dan.jadalnia.app.user.UserSession
import org.dan.jadalnia.app.ws.WsBroadcast
import org.dan.jadalnia.sys.db.DaoUpdater
import org.dan.jadalnia.sys.error.JadEx.Companion.badRequest
import org.dan.jadalnia.sys.error.JadEx.Companion.internalError
import org.dan.jadalnia.util.Futures.Companion.allOf
import org.dan.jadalnia.util.collection.AsyncCache
import org.slf4j.LoggerFactory
import java.util.*
import java.util.Optional.empty
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.completedFuture
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import javax.inject.Named

class OrderService @Inject constructor(
    val wsBroadcast: WsBroadcast,
    @Named("orderCacheByLabel")
    val orderCacheByLabel: AsyncCache<Pair<Fid, OrderLabel>, OrderMem>,
    val orderDao: OrderDao,
    val orderReady: OrderReady,
    val kelnerResigns: KelnerResigns,
    val lowFood: LowFood,
    val customerAbsent: CustomerAbsent,
    val daoUpdater: DaoUpdater,
    val costEstimator: CostEstimator,
    @Named("tokenBalanceCache")
    val tokenBalanceCache: AsyncCache<Pair<Fid, Uid>, TokenBalance>,
    @Named(FESTIVAL_CACHE)
    val festivalCache: AsyncCache<Fid, Festival>,
    val labelService: LabelService) {

  companion object {
    val log = LoggerFactory.getLogger(OrderService::class.java)
  }

  fun showOrderProgressToVisitor(fid: Fid, label: OrderLabel)
      : CompletableFuture<OrderProgress> {
    return orderCacheByLabel.get(Pair(fid, label))
        .thenCompose { orderMem ->
          festivalCache.get(fid).thenCompose { festival ->
            val ordersAhead = countOrdersAhead(festival, label)
            completedFuture(
                OrderProgress(
                    ordersAhead = ordersAhead,
                    etaSeconds = orderEtaInSec(ordersAhead),
                    state = orderMem.state.get()
                ))
          }
        }
  }

  fun countOrdersAhead(festival: Festival, stopOrder: OrderLabel): Int {
    var counter = 0
    for (label in festival.readyToExecOrders) {
      if (stopOrder == label) {
        return counter
      }
      counter += 1
    }
    return -1
  }

  private fun orderEtaInSec(ordersAhead: Int) = ordersAhead * 60

  fun putNewOrder(
      festival: Festival,
      customerSession: UserSession,
      newOrderItems: List<OrderItem>): CompletableFuture<OrderLabel> {
    return labelService
        .allocate(festival)
        .thenApply { label ->
          orderCacheByLabel.inject(
              Pair(festival.fid(), label),
              OrderMem(
                  label = label,
                  customer = customerSession.uid,
                  items = newOrderItems,
                  cost = costEstimator.howMuchFor(festival, newOrderItems),
                  state = AtomicReference(Accepted)
              ))
        }
        .thenCompose { order -> orderDao.storeNewOrder(festival.fid(), order) }
  }

  fun markOrderPaid(festival: Festival, paidOrder: MarkOrderPaid)
      : CompletableFuture<Boolean> {
    return orderCacheByLabel.get(key(festival, paidOrder.label))
        .thenCompose { order ->
          val stWas = order.state.getAndUpdate { st ->
            when (st) {
              Paid -> Paid
              Accepted -> Paid
              else -> throw badRequest(
                  "Order cannot be paid", "label", order.label)
            }
          }
          if (stWas == Accepted) {
            persistPaidOrderAndNotify(festival, order)
                .thenApply { true }
          } else {
            completedFuture(false)
          }
        }
  }

  private fun persistCancelledOrder(festival: Festival, order: OrderMem)
      : CompletableFuture<Unit> {
    return daoUpdater.exec {
      orderDao.updateState(festival.fid(), order.label, Cancelled)
    }
  }

  private fun persistPaidOrderAndNotify(festival: Festival, order: OrderMem)
      : CompletableFuture<Unit> {
    festival.readyToExecOrders.offer(order.label)
    wsBroadcast.broadcastToFreeKelners(
        festival, OrderStateEvent(order.label, Paid))
    return daoUpdater.exec {
      orderDao.updateState(festival.fid(), order.label, Paid)
    }
  }

  private fun key(festival: Festival, label: OrderLabel) = Pair(festival.fid(), label)

  fun kelnerTakenOrderId(festival: Festival, kelnerUid: Uid)
      = completedFuture(Optional.ofNullable(festival.busyKelners[kelnerUid]))

  fun tryToExecOrderWhileNotEmpty(festival: Festival, kelnerUid: Uid)
      : CompletableFuture<Optional<OrderLabel>> {
    return tryToExecOrder(festival, kelnerUid).thenCompose {
      orderO ->
        if (orderO.isPresent || festival.readyToExecOrders.isEmpty()) {
          completedFuture(orderO)
        } else {
          log.info("Kelner {} retries to take order in fid {}",
              kelnerUid, festival.fid())
          tryToExecOrderWhileNotEmpty(festival, kelnerUid)
        }
    }
  }

  fun tryToExecOrder(festival: Festival, kelnerUid: Uid)
      : CompletableFuture<Optional<OrderLabel>> {
    val opLog = OpLog()
    val previousOrder = festival.busyKelners[kelnerUid]
    if (previousOrder != null) {
      throw badRequest("kelner is busy with", "order", previousOrder)
    }
    festival.freeKelners.remove(kelnerUid)
    opLog.add { festival.freeKelners[kelnerUid] = kelnerUid }
    val label = festival.readyToExecOrders.poll()

    if (label == null) {
      log.info("No orders to execute for {}", kelnerUid)
      opLog.rollback()
      return completedFuture(empty())
    }
    opLog.add { festival.readyToExecOrders.offerFirst(label) }
    return orderCacheByLabel.get(Pair(festival.fid(), label))
        .thenCompose { order ->
          log.info("Kelner {} started executing order {}", kelnerUid, label)
          if (order.state.get() == Cancelled) {
            log.info("Skip cancelled order {}:{}", festival.fid(), label)
            completedFuture(empty())
          } else {
            val prevOrder = festival.busyKelners.putIfAbsent(kelnerUid, label)
            if (prevOrder != null) {
              throw badRequest("kelner is busy with", "order", prevOrder)
            }
            opLog.add { festival.busyKelners.remove(kelnerUid, label) }
            if (!order.state.compareAndSet(Paid, Executing)) {
              throw internalError("order is not paid", "state", order.state.get())
            }
            opLog.add { order.state.set(Paid) }
            festival.executingOrders[label] = kelnerUid
            opLog.add { festival.executingOrders.remove(label, kelnerUid) }
            orderDao.assignKelner(festival.fid(), label, kelnerUid, Executing)
                .thenAccept {
                  wsBroadcast.notifyCustomers(
                      festival.fid(),
                      listOf(order.customer),
                      OrderStateEvent(label, Executing))
                }.thenApply { Optional.of(label) }
          }
        }
        .exceptionally { e -> throw opLog.rollback(e) }
  }

  fun showOrderToKelner(fid: Fid, label: OrderLabel) = orderCacheByLabel
      .get(Pair(fid, label))
      .thenApply { order -> KelnerOrderView(order.items) }

  fun showOrderToVisitor(fid: Fid, label: OrderLabel) = orderCacheByLabel
      .get(Pair(fid, label))
      .thenApply { order -> VisitorOrderView(order.label, order.cost, order.state.get()) }

  fun markOrderReadyToPickup(festival: Festival, kelnerUid: Uid, label: OrderLabel)
      = orderReady.complete(festival, kelnerUid, ProblemOrder(label))

  fun pickUpReadyOrder(festival: Festival, customerUid: Uid, label: OrderLabel)
      : CompletableFuture<Uid> {
    val opLog = OpLog()

    if (festival.readyToPickupOrders.remove(label) == null) {
      throw badRequest("Order is not ready", "order", label)
    }
    opLog.add { festival.readyToPickupOrders[label] = Unit }
    return orderCacheByLabel.get(Pair(festival.fid(), label))
        .thenCompose { order ->
          if (!order.state.compareAndSet(Ready, Handed)) {
            throw badRequest("order is not ready", "state", order.state.get())
          }
          opLog.add { order.state.set(Ready) }
          log.info("Customer {} picked up order {}", customerUid, label)
          orderDao.updateState(festival.fid(), label, Handed)
              .thenApply { customerUid  }
        }
        .exceptionally { e -> throw opLog.rollback(e) }
  }

  fun kasierPaysCustomerOrders(festival: Festival, customerUid: Uid)
      : CompletableFuture<List<OrderLabel>> {
    return orderDao.loadUnpaidCustomerOrders(festival.fid(), customerUid)
        .thenCompose { labels ->
          allOf(labels.map {
            label -> customerPays(festival, customerUid, label)
              .thenApply { status -> Pair(label, status) }
          }).thenApply { labeledStatuses ->
            labeledStatuses
                .filter { entry -> entry.second == ORDER_PAID }
                .map { entry -> entry.first }
                .onEach { label ->
                  wsBroadcast.notifyCustomers(
                      festival.fid(),
                      listOf(customerUid),
                      OrderStateEvent(label, Paid))
                }
          }
        }
  }

  private fun customerTryPay(order: OrderMem, festival: Festival,
                             opLog: OpLog, balance: TokenBalance,
                             balanceAmount: TokenPoints)
      : CompletableFuture<PaymentAttemptOutcome> {
    if (balance.effective.compareAndSet(balanceAmount,
            balanceAmount.minus(order.cost))) {
      balance.pending.updateAndGet { p -> p.minus(order.cost) }
      log.info("Balance {} of customer {} is reduced by {}",
          balanceAmount, order.customer, order.cost)
      opLog.add {
        balance.effective.updateAndGet { b -> b.plus(order.cost) }
        balance.pending.updateAndGet { p -> p.plus(order.cost) }
      }
      if (order.state.compareAndSet(Accepted, Paid)) {
        log.info("Fid {}; Status of order {} is Paid",
            festival.fid(), order.label)
        return persistPaidOrderAndNotify(festival, order)
            .thenApply { ORDER_PAID }
            .whenComplete { _, e ->
              if (e != null) {
                opLog.rollback()
                throw internalError("failed persist order status", e)
              }
            }
      } else {
        log.info("Fid {} Status of order {} is already Paid",
            festival.fid(), order.label)
        opLog.rollback()
        return completedFuture(RETRY)
      }
    } else {
      log.info("Fid {} effective balance changed {}",
          festival.fid(), balanceAmount)
      return completedFuture(RETRY)
    }
  }

  fun customerPays(festival: Festival,
                   customerUid: Uid,
                   orderLabel: OrderLabel)
      : CompletableFuture<PaymentAttemptOutcome> {
    if (festival.info.get().state == FestivalState.Close) {
      return completedFuture(FESTIVAL_OVER)
    }
    return orderCacheByLabel.get(Pair(festival.fid(), orderLabel))
        .thenCompose { order ->
          when (order.state.get()) {
            Cancelled -> {
              log.info("Fid {}; Reject payment of order {} due cancelled",
                  festival.fid(), order.label)
              completedFuture(CANCELLED)
            }
            Accepted ->
              tokenBalanceCache
                  .get(Pair(festival.fid(), customerUid))
                  .thenCompose { balance ->
                    val balanceAmount = balance.effective.get();
                    if (balanceAmount.value < order.cost.value) {
                      log.info("Reject payment for order {} due no funds", order.label)
                      completedFuture(NOT_ENOUGH_FUNDS)
                    } else {
                      val opLog = OpLog()
                      customerTryPay(order, festival, opLog, balance, balanceAmount)
                    }
                  }
            else -> {
              log.info("Fid {}; Reject payment of order {} due already paid",
                  festival.fid(), order.label)
              completedFuture(ALREADY_PAID)
            }
          }
        }
  }

  fun listCustomerOrders(fid: Fid, customerUid: Uid)
      : CompletableFuture<List<OrderItemView>> {
    return orderDao.findOrdersForCustomer(fid, customerUid)
  }

  fun countReadyForExec(fest: Festival) = completedFuture(fest.readyToExecOrders.count())

  fun customerDidNotShowUpToStartOrderExecution(
      festival: Festival, kelnerUid: Uid, label: OrderLabel)
      : CompletableFuture<Uid> {
    log.info("Kelner {} reschedules order {} due customer didn't show up",
        kelnerUid, label);
    return customerAbsent.complete(festival, kelnerUid, ProblemOrder(label))
  }

  fun customerReschedules(festival: Festival, orderLabel: OrderLabel)
      : CompletableFuture<PaymentAttemptOutcome> {
    if (festival.info.get().state == FestivalState.Close) {
      return completedFuture(FESTIVAL_OVER)
    }
    return orderCacheByLabel.get(Pair(festival.fid(), orderLabel))
        .thenCompose { order ->
          when (order.state.get()) {
            Cancelled -> {
              log.info("Fid {}; Reject payment of order {} due cancelled",
                  festival.fid(), order.label)
              completedFuture(CANCELLED)
            }
            Paid -> {
              log.info("Fid {}; Order {} is already paid",
                  festival.fid(), order.label)
              completedFuture(ALREADY_PAID)
            }
            Abandoned -> {
              val opLog = OpLog()
              if (order.state.compareAndSet(Abandoned, Paid)) {
                opLog.add { order.state.set(Abandoned) }
                log.info("Fid {}; Status of order {} is Paid",
                    festival.fid(), order.label)
                persistPaidOrderAndNotify(festival, order)
                    .thenApply { ORDER_PAID }
                    .whenComplete { _, e ->
                      if (e != null) {
                        opLog.rollback()
                        throw internalError("failed persist order status", e)
                      }
                    }
              } else {
                log.info("Fid {} Status of order {} is already Paid",
                    festival.fid(), order.label)
                opLog.rollback()
                completedFuture(RETRY)
              }
            }
            else -> {
              log.info("Fid {}; Reject payment of order {} due already paid",
                  festival.fid(), order.label)
              completedFuture(ALREADY_PAID)
            }
          }
        }
  }

  fun kelnerWithAssignedOrderResigns(
      festival: Festival, kelnerUid: Uid, label: OrderLabel)
      : CompletableFuture<Uid> {
    return kelnerResigns.complete(festival, kelnerUid, ProblemOrder(label))
  }

  fun kelnerCannotCompleteOrderDueNoReadyMeal(
      festival: Festival, uid: Uid,
      problemOrder: ProblemOrder)
      : CompletableFuture<Uid> {
    return lowFood.complete(festival, uid, problemOrder)
  }

  fun resumeOrdersBackward(festival: Festival, orders: LinkedList<OrderLabel>)
      : CompletableFuture<Void> {
    if (orders.isEmpty()) {
      return completedFuture(null)
    }
    val label = orders.removeLast()
    val fid = festival.fid()

    return orderCacheByLabel.get(Pair(festival.fid(), label))
        .thenCompose { order ->
          if (order.state.compareAndSet(Delayed, Paid)) {
            log.info("Move delayed order {}:{} to exec queue", fid, label)
            wsBroadcast.broadcastToFreeKelners(
                festival, OrderStateEvent(label, Paid))
            wsBroadcast.notifyCustomers(
                festival.fid(),
                listOf(order.customer),
                OrderStateEvent(label, Paid))
            festival.readyToExecOrders.offerFirst(label)
            daoUpdater.exec {
              orderDao.updateState(festival.fid(), label, Paid)
            }.thenCompose {
              resumeOrdersBackward(festival, orders)
            }
          } else {
            log.info("Drop delayed order {}:{} due not Delayed but state {} ",
                fid, label, order.state.get())
            resumeOrdersBackward(festival, orders)
          }
        }
  }

  fun resumeOrdersWithMeal(festival: Festival, meal: DishName)
      : CompletableFuture<Int> {
    val fid = festival.fid()
    val suspendedOrders = festival.queuesForMissingMeals.takeAll(meal)
    val size = suspendedOrders.size
    log.info("Meal {}:{} was blocking {} orders", fid, meal, size)

    return resumeOrdersBackward(festival, suspendedOrders).thenApply { size }
  }

  private fun customerTryCancel(order: OrderMem, festival: Festival,
                                opLog: OpLog, balance: TokenBalance,
                                balanceAmount: TokenPoints,
                                orderState: OrderState)
      : CompletableFuture<CancelAttemptOutcome> {
    if (balance.effective.compareAndSet(balanceAmount,
            balanceAmount.plus(order.cost))) {
      balance.pending.updateAndGet { p -> p.plus(order.cost) }
      log.info("Balance {} of customer {} is increased by {}",
          balanceAmount, order.customer, order.cost)
      opLog.add {
        balance.effective.updateAndGet { b -> b.minus(order.cost) }
        balance.pending.updateAndGet { p -> p.minus(order.cost) }
      }
      if (order.state.compareAndSet(orderState, Cancelled)) {
        log.info("Fid {}; Status of order {} is Cancelled",
            festival.fid(), order.label)
        return persistCancelledOrder(festival, order)
            .thenApply { CancelAttemptOutcome.CANCELLED }
            .whenComplete { _, e ->
              if (e != null) {
                opLog.rollback()
                throw internalError("failed persist order status", e)
              }
            }
      } else {
        log.info("Fid {} Status of order {} is already Paid",
            festival.fid(), order.label)
        opLog.rollback()
        return completedFuture(CancelAttemptOutcome.RETRY)
      }
    } else {
      log.info("Fid {} effective balance changed {}",
          festival.fid(), balanceAmount)
      return completedFuture(CancelAttemptOutcome.RETRY)
    }
  }

  fun customerCancels(festival: Festival, orderLabel: OrderLabel)
      : CompletableFuture<CancelAttemptOutcome> {
    return orderCacheByLabel.get(Pair(festival.fid(), orderLabel))
        .thenCompose { order ->
          val orderState = order.state.get()
          when (orderState) {
            Cancelled -> {
              log.info("Fid {}; Order {} was cancelled before",
                  festival.fid(), order.label)
              completedFuture(CancelAttemptOutcome.CANCELLED)
            }
            Accepted -> {
              log.info("Fid {}; Cancel accepted order {}",
                  festival.fid(), order.label)
              val opLog = OpLog()
              if (order.state.compareAndSet(Accepted, Cancelled)) {
                opLog.add { order.state.set(Accepted) }
                log.info("Fid {}; Status of order {} is Cancelled",
                    festival.fid(), order.label)
                persistCancelledOrder(festival, order)
                    .thenApply { CancelAttemptOutcome.CANCELLED }
                    .whenComplete { _, e ->
                      if (e != null) {
                        opLog.rollback()
                        throw internalError("failed persist order status", e)
                      }
                    }
              } else {
                log.info("Fid {} Status of order {} is changed",
                    festival.fid(), order.label)
                opLog.rollback()
                completedFuture(CancelAttemptOutcome.RETRY)
              }
            }
            Paid, Delayed, Abandoned -> {
              tokenBalanceCache
                  .get(Pair(festival.fid(), order.customer))
                  .thenCompose { balance ->
                    val balanceAmount = balance.effective.get();
                    val opLog = OpLog()
                    customerTryCancel(order, festival, opLog,
                        balance, balanceAmount, orderState)
                  }
            }
            else -> {
              log.info("Fid {}; Order {} is not in cancellable state {}",
                  festival.fid(), order.label, orderState)
              completedFuture(CancelAttemptOutcome.NOT_CANCELLED)
            }
          }
        }
  }
}
