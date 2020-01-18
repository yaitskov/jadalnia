package org.dan.jadalnia.app.order.complete

import org.dan.jadalnia.app.festival.pojo.Festival
import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.order.OpLog
import org.dan.jadalnia.app.order.OrderDao
import org.dan.jadalnia.app.order.pojo.OrderLabel
import org.dan.jadalnia.app.order.pojo.OrderMem
import org.dan.jadalnia.app.order.pojo.OrderState
import org.dan.jadalnia.app.ws.WsBroadcast
import org.dan.jadalnia.sys.error.JadEx.Companion.internalError
import org.dan.jadalnia.util.collection.AsyncCache
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.completedFuture
import javax.inject.Inject

class KelnerResigns @Inject constructor(
    wsBroadcast: WsBroadcast,
    orderDao: OrderDao,
    orderCacheByLabel: AsyncCache<Pair<Fid, OrderLabel>, OrderMem>)
  : BaseOrderCompleteStrategy(wsBroadcast, orderDao, orderCacheByLabel) {

  override val targetState: OrderState = OrderState.Paid

  override fun updateTargetState(festival: Festival, label: OrderLabel, opLog: OpLog)
      : CompletableFuture<Void> {

    if (!festival.readyToExecOrders.offerFirst(label)) {
      throw internalError("Failed to return order back to exec queue", "label", label)
    }
    return completedFuture(null)
  }
}