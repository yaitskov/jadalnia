package org.dan.jadalnia.app.order

import com.google.common.collect.ImmutableSet
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
    val delayedOrderDao: DelayedOrderDao,
    val orderReady: OrderReady,
    val kelnerResigns: KelnerResigns,
    val lowFood: LowFood,
    val customerAbsent: CustomerAbsent,
    val costEstimator: CostEstimator,
    @Named("tokenBalanceCache")
    val tokenBalanceCache: AsyncCache<Pair<Fid, Uid>, TokenBalance>,
    @Named(FESTIVAL_CACHE)
    val festivalCache: AsyncCache<Fid, Festival>,
    val labelService: LabelService) {

  companion object {
    val log = LoggerFactory.getLogger(OrderService::class.java)
    val skipOrderStates = ImmutableSet.of(
        // order can have all these state due order label could be duplicated
        // in line and selected by 2 kelners
        Executing, Ready, Cancelled, Handed, Abandoned, Delayed)
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
                  items = AtomicReference(newOrderItems),
                  cost = AtomicReference(costEstimator.howMuchFor(festival, newOrderItems)),
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
    return orderDao.updateState(festival.fid(), order.label, Cancelled)
  }

  private fun persistPaidOrderAndNotify(festival: Festival, order: OrderMem)
      : CompletableFuture<Unit> {
    festival.readyToExecOrders.offer(order.label)
    wsBroadcast.broadcastToFreeKelners(
        festival, OrderStateEvent(order.label, Paid))
    return orderDao.updateState(festival.fid(), order.label, Paid)
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
          if (skipOrderStates.contains(order.state.get())) {
            log.info("Skip order {}:{} due state {}",
                festival.fid(), label, order.state.get())
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
      .thenApply { order -> KelnerOrderView(order.items.get()) }

  fun showOrderToVisitor(fid: Fid, label: OrderLabel) = orderCacheByLabel
      .get(Pair(fid, label))
      .thenApply { order ->
        VisitorOrderView(
            order.label, order.cost.get(),
            order.state.get(), order.items.get())
      }

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

  private fun updateBalance(order: OrderMem, fid: Fid,
                            opLog: OpLog, balance: TokenBalance,
                            balanceAmount: TokenPoints,
                            update: TokenPoints)
      : Boolean {
    if (balance.effective.compareAndSet(balanceAmount,
            balanceAmount.minus(update))) {
      balance.pending.updateAndGet { p -> p.minus(update) }
      log.info("Balance {} of customer {}:{} is updated by {}",
          balanceAmount, fid, order.customer, update)
      opLog.add {
        balance.effective.updateAndGet { b -> b.plus(update) }
        balance.pending.updateAndGet { p -> p.plus(update) }
      }
      return true
    } else {
      return false
    }
  }

  private fun customerTryPay(order: OrderMem, festival: Festival,
                             opLog: OpLog, balance: TokenBalance,
                             balanceAmount: TokenPoints,
                             orderCost: TokenPoints)
      : CompletableFuture<PaymentAttemptOutcome> {
    if (balance.effective.compareAndSet(balanceAmount,
            balanceAmount.minus(orderCost))) {
      balance.pending.updateAndGet { p -> p.minus(orderCost) }
      log.info("Balance {} of customer {} is reduced by {}",
          balanceAmount, order.customer, orderCost)
      opLog.add {
        balance.effective.updateAndGet { b -> b.plus(orderCost) }
        balance.pending.updateAndGet { p -> p.plus(orderCost) }
      }
      if (order.cost.get() != orderCost) {
        log.info("Rollback pay {}:{} due cost change {} != {}. Retry.",
            festival.fid(), order.label, order.cost.get(), orderCost)
        opLog.rollback()
        return completedFuture(RETRY)
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
                    val orderCost = order.cost.get()
                    if (balanceAmount.value < orderCost.value) {
                      log.info("Reject payment for order {} due no funds", order.label)
                      completedFuture(NOT_ENOUGH_FUNDS)
                    } else {
                      val opLog = OpLog()
                      customerTryPay(order, festival, opLog, balance, balanceAmount, orderCost)
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
            orderDao.updateState(festival.fid(), label, Paid).thenCompose {
              delayedOrderDao.remove(festival.fid(), label).thenCompose {
                resumeOrdersBackward(festival, orders)
              }
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
    val orderCost = order.cost.get()
    if (balance.effective.compareAndSet(balanceAmount,
            balanceAmount.plus(orderCost))) {
      balance.pending.updateAndGet { p -> p.plus(orderCost) }
      log.info("Balance {} of customer {} is increased by {}",
          balanceAmount, order.customer, order.cost)
      opLog.add {
        balance.effective.updateAndGet { b -> b.minus(orderCost) }
        balance.pending.updateAndGet { p -> p.minus(orderCost) }
      }
      if (orderCost != order.cost.get()) {
        log.info("Rollback cancel {}:{} due cost change {} != {}. Retry.",
            festival.fid(), order.label, order.cost.get(), orderCost)
        opLog.rollback()
        return completedFuture(CancelAttemptOutcome.RETRY)
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

  fun modifyDelayedOrder(op: OpLog, order: OrderMem,
                         festival: Festival, update: OrderUpdate,
                         newCost: TokenPoints)
      : CompletableFuture<UpdateAttemptOutcome> {
    return modifyPaidOrder(op, order, festival, update, newCost)
        .thenCompose { outcome ->
          if (outcome == UpdateAttemptOutcome.UPDATED) {
            val missingMeals = festival.queuesForMissingMeals.keys()
            if (!update.newItems.any { item -> missingMeals.contains(item.name) }
                && order.state.compareAndSet(Delayed, Paid)) {
              festival.readyToExecOrders.addFirst(order.label)
              delayedOrderDao.remove(festival.fid(), order.label).thenCompose {
                orderDao.updateState(festival.fid(), order.label, Paid)
              }.thenApply {
                outcome
              }
            } else {
              completedFuture(outcome)
            }
          } else {
            completedFuture(outcome)
          }
        }
  }

  fun modifyPaidOrder(op: OpLog, order: OrderMem,
                          festival: Festival, update: OrderUpdate,
                          newCost: TokenPoints)
      : CompletableFuture<UpdateAttemptOutcome> {
    val wasItems = order.items.get()
    return tokenBalanceCache
        .get(Pair(festival.fid(), order.customer))
        .thenCompose { balance ->
          val balanceAmount = balance.effective.get();
          val orderCost = order.cost.get()
          val diff = newCost.minus(orderCost)
          if (balanceAmount.value < diff.value) {
            log.info("Reject order {}:{} update due no funds",
                festival.fid(), order.label)
            completedFuture(UpdateAttemptOutcome.NOT_ENOUGH_FUNDS)
          } else {
            if (updateBalance(order, festival.fid(), op, balance, balanceAmount, diff)) {
              if (order.cost.compareAndSet(orderCost, newCost)) {
                op.add { order.cost.set(orderCost) }
                if (order.items.compareAndSet(wasItems, update.newItems)) {
                  orderDao.updateCostAndItems(festival.fid(), newCost, update).thenApply {
                    UpdateAttemptOutcome.UPDATED
                  }
                } else {
                  op.rollback()
                  completedFuture(UpdateAttemptOutcome.RETRY)
                }
              } else {
                log.info("Rollback paid order modification {}:{} due cost change {} != {}. Retry.",
                    festival.fid(), order.label, order.cost.get(), update)
                op.rollback()
                 completedFuture(UpdateAttemptOutcome.RETRY)
              }
            } else {
              op.rollback()
              completedFuture(UpdateAttemptOutcome.RETRY)
            }
          }
        }
  }

  fun modifyAcceptedOrder(op: OpLog, order: OrderMem,
                          festival: Festival, update: OrderUpdate,
                          newCost: TokenPoints)
      : CompletableFuture<UpdateAttemptOutcome> {
    val wasCost = order.cost.get()
    val wasItems = order.items.get()
    if (order.cost.compareAndSet(wasCost, newCost)) {
      op.add { order.cost.set(wasCost) }
      if (order.items.compareAndSet(wasItems, update.newItems)) {
        return orderDao.updateCostAndItems(festival.fid(), newCost, update).thenApply {
          UpdateAttemptOutcome.UPDATED
        }
      } else {
        op.rollback()
        return completedFuture(UpdateAttemptOutcome.RETRY)
      }
    } else {
      op.rollback()
      return completedFuture(UpdateAttemptOutcome.RETRY)
    }
  }

  fun modifyOrder(festival: Festival, update: OrderUpdate)
      : CompletableFuture<UpdateAttemptOutcome> {
    if (festival.info.get().state == FestivalState.Close) {
      return completedFuture(UpdateAttemptOutcome.FESTIVAL_OVER)
    }
    val newCost = costEstimator.howMuchFor(festival, update.newItems)
    val orderLabel = update.label
    val op = OpLog()
    return orderCacheByLabel.get(Pair(festival.fid(), orderLabel))
        .thenCompose { order ->
          when (order.state.get()) {
            Cancelled -> {
              log.info("Fid {}; Reject order update {} due cancelled",
                  festival.fid(), order.label)
              completedFuture(UpdateAttemptOutcome.BAD_ORDER_STATE)
            }
            Accepted -> modifyAcceptedOrder(op, order, festival, update, newCost)
            Paid -> modifyPaidOrder(op, order, festival, update, newCost)
            Delayed -> modifyDelayedOrder(op, order, festival, update, newCost)
            else -> {
              log.info("Fid {}; Reject order update {} due bad state {}",
                  festival.fid(), order.label, order.state.get())
              completedFuture(UpdateAttemptOutcome.BAD_ORDER_STATE)
            }
          }
        }
  }
}
