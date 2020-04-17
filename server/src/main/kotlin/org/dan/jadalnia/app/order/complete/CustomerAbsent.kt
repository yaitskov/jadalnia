package org.dan.jadalnia.app.order.complete

import org.dan.jadalnia.app.festival.pojo.Festival
import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.festival.pojo.Taca
import org.dan.jadalnia.app.festival.pojo.TacaExec
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
import java.util.concurrent.CompletableFuture.completedFuture
import javax.inject.Inject

class CustomerAbsent @Inject constructor(
    clocker: Clocker,
    wsBroadcast: WsBroadcast,
    orderDao: OrderDao,
    orderCacheByLabel: AsyncCache<Pair<Fid, OrderLabel>, OrderMem>)
  : BaseOrderCompleteStrategy(clocker, wsBroadcast, orderDao, orderCacheByLabel) {

  override val targetState = OrderState.Abandoned

  override fun updateTargetState(
      festival: Festival, problemOrder: ProblemOrder, opLog: OpLog, tacaPair: Pair<Taca, TacaExec>, order: OrderMem)
      : CompletableFuture<Optional<MapQ.QueueInsertIdx>> {
    // user has to reinitiate manually
    log.info("Order {} in {} is abandoned", problemOrder.label, festival.fid())
    return completedFuture(Optional.empty())
  }
}