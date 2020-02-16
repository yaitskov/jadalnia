package org.dan.jadalnia.app.order.complete

import org.dan.jadalnia.app.festival.pojo.Festival
import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.order.OpLog
import org.dan.jadalnia.app.order.OrderDao
import org.dan.jadalnia.app.order.OrderStateEvent
import org.dan.jadalnia.app.order.pojo.OrderLabel
import org.dan.jadalnia.app.order.pojo.OrderMem
import org.dan.jadalnia.app.order.pojo.OrderState
import org.dan.jadalnia.app.order.pojo.OrderState.Executing
import org.dan.jadalnia.app.order.pojo.ProblemOrder
import org.dan.jadalnia.app.user.Uid
import org.dan.jadalnia.app.ws.WsBroadcast
import org.dan.jadalnia.sys.error.JadEx.Companion.badRequest
import org.dan.jadalnia.sys.error.JadEx.Companion.internalError
import org.dan.jadalnia.util.collection.AsyncCache
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.completedFuture


abstract class BaseOrderCompleteStrategy(
    val wsBroadcast: WsBroadcast,
    val orderDao: OrderDao,
    val orderCacheByLabel: AsyncCache<Pair<Fid, OrderLabel>, OrderMem>)
  : OrderCompleteStrategy {

  companion object {
    val log = LoggerFactory.getLogger(BaseOrderCompleteStrategy::class.java)
  }

  fun freeOrderAndKelner(festival: Festival, kelnerUid: Uid, label: OrderLabel): OpLog {
    val opLog = OpLog()
    if (!festival.busyKelners.remove(kelnerUid, label)) {
      throw badRequest("kelner was not busy with", "order", label)
    }
    opLog.add { festival.busyKelners[kelnerUid] = label }
    festival.freeKelners[kelnerUid] = kelnerUid
    opLog.add { festival.freeKelners.remove(kelnerUid) }
    if (!festival.executingOrders.remove(label, kelnerUid)) {
      opLog.rollback()
      throw internalError(
          "Order was not executed by",
          mapOf(Pair("kelner", kelnerUid),
              Pair("order", label)))
    }
    opLog.add { festival.executingOrders[label] = kelnerUid }
    return opLog
  }

  override fun complete(festival: Festival, kelnerUid: Uid, problemOrder: ProblemOrder)
      : CompletableFuture<Uid> {
    val label = problemOrder.label
    val opLog = freeOrderAndKelner(festival, kelnerUid, label)
    return orderCacheByLabel.get(Pair(festival.fid(), label))
        .thenCompose { order ->
          if (!order.state.compareAndSet(Executing, targetState)) {
            throw internalError("order is not executing", "state", order.state.get())
          }
          opLog.add { order.state.set(Executing) }
          log.info("Kelner {} completed executing order {} as {}",
              kelnerUid, label, targetState)
          updateTargetState(festival, problemOrder, opLog).thenCompose {
            orderDao.assignKelner(festival.fid(), label, kelnerUid, targetState)
                .thenAccept {
                  wsBroadcast.notifyCustomers(
                      festival.fid(),
                      listOf(order.customer),
                      OrderStateEvent(label, targetState))
                }
            completedFuture(kelnerUid)
          }
        }
        .exceptionally { e -> throw opLog.rollback(e) }
  }

  abstract val targetState: OrderState;

  abstract fun updateTargetState(
      festival: Festival, problemOrder: ProblemOrder, opLog: OpLog)
      : CompletableFuture<Void>;
}