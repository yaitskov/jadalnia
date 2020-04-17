package org.dan.jadalnia.app.order.line

import org.dan.jadalnia.app.festival.menu.DishName
import org.dan.jadalnia.app.festival.pojo.FestParams
import org.dan.jadalnia.app.festival.pojo.Festival
import org.dan.jadalnia.app.festival.pojo.Taca
import org.dan.jadalnia.app.user.Uid
import org.dan.jadalnia.util.collection.MapQ

class OrderExecTimeEstimator {
  fun estimateFor(festival: Festival, queueIdx: MapQ.QueueInsertIdx,
                  activeKelners: Set<Uid>, params: FestParams)
      : OrderExecEstimate {
    return festival.estimatorState.estimate(
        activeKelners, params,
        aggregateDemand(queueIdx, festival.readyToExecOrders))
  }


}