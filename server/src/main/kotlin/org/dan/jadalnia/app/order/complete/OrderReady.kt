package org.dan.jadalnia.app.order.complete

import org.dan.jadalnia.app.festival.pojo.Festival
import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.order.OpLog
import org.dan.jadalnia.app.order.OrderDao
import org.dan.jadalnia.app.order.pojo.OrderLabel
import org.dan.jadalnia.app.order.pojo.OrderMem
import org.dan.jadalnia.app.order.pojo.OrderState
import org.dan.jadalnia.app.order.pojo.ProblemOrder
import org.dan.jadalnia.app.ws.WsBroadcast
import org.dan.jadalnia.util.collection.AsyncCache
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.completedFuture
import javax.inject.Inject

class OrderReady @Inject constructor(
    wsBroadcast: WsBroadcast,
    orderDao: OrderDao,
    orderCacheByLabel: AsyncCache<Pair<Fid, OrderLabel>, OrderMem>)
  : BaseOrderCompleteStrategy(wsBroadcast, orderDao, orderCacheByLabel) {

  override val targetState: OrderState = OrderState.Ready

  override fun updateTargetState(
      festival: Festival, problemOrder: ProblemOrder, opLog: OpLog)
      : CompletableFuture<Void> {
    val label = problemOrder.label
    festival.readyToPickupOrders[label] = Unit
    opLog.add { festival.readyToPickupOrders.remove(label) }

    if (festival.queuesForMissingMeals.isEmpty()) {
      return completedFuture(null)
    }

    return orderCacheByLabel.get(Pair(festival.fid(), label))
        .thenApply { order ->
          order.items.get().forEach { item ->
            val suspendedOrders = festival.queuesForMissingMeals.takeAll(item.name)
            if (!suspendedOrders.isEmpty()) {
              log.info("Move meals {} to main queue", item.name)
              suspendedOrders.forEach { label ->
                log.info("Move {} to missing meals queue to main", label)
                festival.readyToExecOrders.offerFirst(label)
              }
            }
          }
          null
        }
  }
}