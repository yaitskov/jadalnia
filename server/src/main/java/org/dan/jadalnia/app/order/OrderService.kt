package org.dan.jadalnia.app.order

import org.dan.jadalnia.app.festival.pojo.Festival
import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.label.LabelService
import org.dan.jadalnia.app.order.pojo.MarkOrderPaid
import org.dan.jadalnia.app.order.pojo.OrderItem
import org.dan.jadalnia.app.order.pojo.OrderLabel
import org.dan.jadalnia.app.order.pojo.OrderMem
import org.dan.jadalnia.app.order.pojo.OrderState.Accepted
import org.dan.jadalnia.app.order.pojo.OrderState.Executing
import org.dan.jadalnia.app.order.pojo.OrderState.Handed
import org.dan.jadalnia.app.order.pojo.OrderState.Paid
import org.dan.jadalnia.app.order.pojo.OrderState.Ready
import org.dan.jadalnia.app.user.Uid
import org.dan.jadalnia.app.user.UserSession
import org.dan.jadalnia.app.ws.WsBroadcast
import org.dan.jadalnia.sys.db.DaoUpdater
import org.dan.jadalnia.sys.error.JadEx.Companion.badRequest
import org.dan.jadalnia.sys.error.JadEx.Companion.internalError
import org.dan.jadalnia.util.collection.AsyncCache
import org.slf4j.LoggerFactory
import java.util.Optional
import java.util.Optional.empty
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.completedFuture
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

class OrderService @Inject constructor(
    val wsBroadcast: WsBroadcast,
    val orderCacheByLabel: AsyncCache<Pair<Fid, OrderLabel>, OrderMem>,
    val orderDao: OrderDao,
    val daoUpdater: DaoUpdater,
    val labelService: LabelService) {

  companion object {
    val log = LoggerFactory.getLogger(OrderService::class.java)
  }

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
                  state = AtomicReference(Accepted)
              ))
        }
        .thenCompose { order -> orderDao.storeNewOrder(festival.fid(), order) }
  }

  fun markOrderPaid(festival: Festival, paidOrder: MarkOrderPaid)
      : CompletableFuture<Boolean> {
    return orderCacheByLabel.get(key(festival, paidOrder.label))
        .thenApply { order ->
          val stWas = order.state.getAndUpdate { st ->
            when (st) {
              Paid -> Paid
              Accepted -> Paid
              else -> throw badRequest(
                  "Order cannot be paid", "label", order.label)
            }
          }
          if (stWas == Accepted) {
            festival.readyToExecOrders.offer(order.label)
            wsBroadcast.broadcastToFreeKelners(
                festival, OrderPaidEvent(order.label))
            daoUpdater.exec { orderDao.updateState(festival.fid(), order.label, Paid) }
            true
          } else {
            false
          }
        }
  }

  private fun key(festival: Festival, label: OrderLabel) = Pair(festival.fid(), label)

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
          val prevOrder = festival.busyKelners.putIfAbsent(kelnerUid, label)
          if (prevOrder != null) {
            throw badRequest("kelner is busy with", "order", prevOrder)
          }
          opLog.add { festival.busyKelners.remove(kelnerUid, label) }
          festival.executingOrders[label] = kelnerUid
          opLog.add { festival.executingOrders.remove(label, kelnerUid) }
          orderDao.assignKelner(festival.fid(), label, kelnerUid)
              .thenAccept {
                wsBroadcast.notifyCustomers(
                    festival.fid(), listOf(order.customer), OrderStateEvent(label, Executing))
              }.thenApply { Optional.of(label) }
        }
        .exceptionally { e -> throw opLog.rollback(e) }
  }

  fun showOrderToKelner(fid: Fid, label: OrderLabel) = orderCacheByLabel
      .get(Pair(fid, label))
      .thenApply { order -> KelnerOrderView(order.items) }

  fun markOrderReadyToPickup(festival: Festival, kelnerUid: Uid, label: OrderLabel)
      : CompletableFuture<Void> {
    val opLog = OpLog()
    if (!festival.busyKelners.remove(kelnerUid, label)) {
      throw badRequest("kelner was not busy with", "order", label)
    }
    opLog.add { festival.busyKelners[kelnerUid] = label }
    festival.freeKelners[kelnerUid] = kelnerUid

    if (!festival.executingOrders.remove(label, kelnerUid)) {
      opLog.rollback()
      throw internalError(
          "Order was not executed by",
          mapOf(Pair("kelner", kelnerUid),
              Pair("order", label)))
    }
    opLog.add { festival.executingOrders[label] = kelnerUid }
    return orderCacheByLabel.get(Pair(festival.fid(), label))
        .thenCompose { order ->
          log.info("Kelner {} completed executing order {}", kelnerUid, label)
          festival.freeKelners[kelnerUid] = kelnerUid
          opLog.add { festival.freeKelners.remove(kelnerUid) }
          festival.readyToPickupOrders[label] = Unit
          opLog.add { festival.readyToPickupOrders.remove(label) }
          orderDao.updateState(festival.fid(), label, Ready)
              .thenAccept {
                wsBroadcast.notifyCustomers(
                    festival.fid(), listOf(order.customer), OrderStateEvent(label, Ready))
              }
        }
        .exceptionally { e -> throw opLog.rollback(e) }
  }

  fun pickUpReadyOrder(festival: Festival, customerUid: Uid, label: OrderLabel)
      : CompletableFuture<Void> {
    val opLog = OpLog()

    if (festival.readyToPickupOrders.remove(label) == null) {
      throw badRequest("Order is  not ready", "order", label)
    }
    opLog.add { festival.readyToPickupOrders[label] = Unit }
    return orderCacheByLabel.get(Pair(festival.fid(), label))
        .thenCompose { order ->
          log.info("Customer {} picked up order {}", customerUid, label)
          orderDao.updateState(festival.fid(), label, Handed)
              .thenAccept {  }
        }
        .exceptionally { e -> throw opLog.rollback(e) }
  }
}
