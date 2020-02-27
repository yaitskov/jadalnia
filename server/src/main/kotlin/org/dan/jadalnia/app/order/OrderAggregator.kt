package org.dan.jadalnia.app.order

import org.dan.jadalnia.app.festival.pojo.Festival
import org.dan.jadalnia.app.order.line.OrderExecTimeEstimator
import org.dan.jadalnia.app.order.pojo.OrderMem
import org.dan.jadalnia.util.collection.MapQ

class OrderAggregator {
  private fun findStateIdx(enqueuePosition: Int): Int {
    val idx = OrderExecTimeEstimator.indexes
        .binarySearch(enqueuePosition)
    if (idx >= 0 && idx + 1 < OrderExecTimeEstimator.indexes.size) {
      return idx + 1
    } else if (idx < 0) {
      val modIdx = -idx - 1
      if (modIdx < OrderExecTimeEstimator.indexes.size) {
        return modIdx
      }
    }
    return -1 // skip - line is too long
  }

  fun aggregateIn(festival: Festival,
                  queueInsertIdx: MapQ.QueueInsertIdx,
                  order: OrderMem) {
    val position = festival.readyToExecOrders.positionByIdx(queueInsertIdx)
    val idx = findStateIdx(position)

    if (idx >= 0) {
      val state = festival.estimatorState
      val items = order.items.get().associate { orderItem ->
        Pair(orderItem.name, orderItem.quantity)
      }
      val statPos = state.mealsAgg.getAggOrNew(
          OrderExecTimeEstimator.indexes[idx])
      statPos.orderMeals2Count.incrementAndGet(items)
    }
  }

  fun aggregateOut() {

  }
}