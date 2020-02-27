package org.dan.jadalnia.app.order.complete

import org.dan.jadalnia.app.festival.pojo.Festival
import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.festival.pojo.Taca
import org.dan.jadalnia.app.order.DelayedOrderDao
import org.dan.jadalnia.app.order.OpLog
import org.dan.jadalnia.app.order.OrderDao
import org.dan.jadalnia.app.order.pojo.OrderLabel
import org.dan.jadalnia.app.order.pojo.OrderMem
import org.dan.jadalnia.app.order.pojo.OrderState
import org.dan.jadalnia.app.order.pojo.ProblemOrder
import org.dan.jadalnia.app.ws.WsBroadcast
import org.dan.jadalnia.util.collection.AsyncCache
import org.dan.jadalnia.util.collection.MapQ
import org.dan.jadalnia.util.time.Clocker
import java.util.*
import java.util.concurrent.CompletableFuture
import javax.inject.Inject

class LowFood @Inject constructor(
    val delayedOrderDao: DelayedOrderDao,
    clocker: Clocker,
    wsBroadcast: WsBroadcast,
    orderDao: OrderDao,
    orderCacheByLabel: AsyncCache<Pair<Fid, OrderLabel>, OrderMem>)
  : BaseOrderCompleteStrategy(clocker, wsBroadcast, orderDao, orderCacheByLabel) {

  override val targetState: OrderState = OrderState.Delayed

  override fun updateTargetState(
      festival: Festival, problemOrder: ProblemOrder,
      opLog: OpLog, taca: Taca)
      : CompletableFuture<Optional<MapQ.QueueInsertIdx>> {
    festival.queuesForMissingMeals.put(
        problemOrder.meal!!, taca)
    opLog.add {
      festival.queuesForMissingMeals.remove(problemOrder.meal!!, taca)
    }
    return delayedOrderDao
        .delayed(festival.fid(), problemOrder.meal!!, problemOrder.label)
        .thenApply { Optional.empty<MapQ.QueueInsertIdx>() }
  }
}