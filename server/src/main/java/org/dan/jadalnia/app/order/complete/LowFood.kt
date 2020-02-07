package org.dan.jadalnia.app.order.complete

import org.dan.jadalnia.app.festival.pojo.Festival
import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.order.DelayedOrderDao
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

class LowFood @Inject constructor(
    val delayedOrderDao: DelayedOrderDao,
    wsBroadcast: WsBroadcast,
    orderDao: OrderDao,
    orderCacheByLabel: AsyncCache<Pair<Fid, OrderLabel>, OrderMem>)
  : BaseOrderCompleteStrategy(wsBroadcast, orderDao, orderCacheByLabel) {

  override val targetState: OrderState = OrderState.Delayed

  override fun updateTargetState(
      festival: Festival, problemOrder: ProblemOrder, opLog: OpLog)
      : CompletableFuture<Void> {
    festival.queuesForMissingMeals.put(
        problemOrder.meal!!, problemOrder.label)
    delayedOrderDao.delayed(festival.fid(), problemOrder.meal!!, problemOrder.label)
    opLog.add {
      festival.queuesForMissingMeals.remove(problemOrder.meal!!, problemOrder.label)
    }
    // notify admin
    return completedFuture(null);
  }
}