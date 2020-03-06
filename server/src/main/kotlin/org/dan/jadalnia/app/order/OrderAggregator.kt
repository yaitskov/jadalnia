package org.dan.jadalnia.app.order

import org.dan.jadalnia.app.festival.menu.DishName
import org.dan.jadalnia.app.festival.pojo.Festival
import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.festival.pojo.Taca
import org.dan.jadalnia.app.order.line.OrderExecTimeEstimator
import org.dan.jadalnia.app.order.line.OrderExecTimeEstimator.Companion.indexes
import org.dan.jadalnia.app.order.line.QueueRangeState
import org.dan.jadalnia.app.order.line.QueueRanges
import org.dan.jadalnia.app.order.pojo.OrderLabel
import org.dan.jadalnia.app.order.pojo.OrderMem
import org.dan.jadalnia.util.collection.AsyncCache
import org.dan.jadalnia.util.collection.MapQ
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.completedFuture
import javax.inject.Inject
import javax.inject.Named

class OrderAggregator @Inject constructor (
    @Named("orderCacheByLabel")
    val orderCacheByLabel: AsyncCache<Pair<Fid, OrderLabel>, OrderMem>) {

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
      val items = toMap(order)
      val statPos = state.mealsAgg.getAggOrNew(
          OrderExecTimeEstimator.indexes[idx])
      statPos.orderMeals2Count.incrementAndGet(items)
    } else {
      // skip
    }
  }

  fun toMap(o: OrderMem): Map<DishName, Int> = o.items.get()
      .associate { orderItem ->
        Pair(orderItem.name, orderItem.quantity)
      }

  fun moveMatchingOrderBy1Up(
      fid: Fid,
      mealsAgg: QueueRanges,
      readyToExecOrders: MapQ<Taca>,
      queueInsertIdx0: MapQ.QueueInsertIdx,
      positionIdx: Int, positionAggState: QueueRangeState?)
      : CompletableFuture<QueueRangeState?> {
    val idx = queueInsertIdx0.plus(indexes[positionIdx])
    val taca = readyToExecOrders[idx]
    if (taca == null) {
      // find order by (fid, idx) -> idx not unique in persistence
      // due interleave agg out and agg in head
      // order was served or cancelled
      return completedFuture(null)
    } else {
      return orderCacheByLabel.get(Pair(fid, taca.label)).thenApply { order ->
        val items = toMap(order)
        if (positionAggState != null) {
          positionAggState.orderMeals2Count.decrementAndGet(items)
        }
        if (indexes.size > positionIdx + 1) {
          val rangeStateInc = mealsAgg.getAggOrNew(indexes[positionIdx + 1])
          rangeStateInc.orderMeals2Count.incrementAndGet(items)
          rangeStateInc
        } else {
          null
        }
      }
    }
  }

  fun aggregateHeadIn(festival: Festival,
                      queueInsertIdx0: MapQ.QueueInsertIdx,
                      order: OrderMem) {
    val fid = festival.fid()

    addOrderToAllExistingRanges(order, festival)
    val state = festival.estimatorState
    val queue = festival.readyToExecOrders
    OrderExecTimeEstimator.indexes.forEach { pos ->
      val idx = queueInsertIdx0.plus(pos)
      val taca = queue[idx]
      if (taca != null) {
        orderCacheByLabel.get(Pair(fid, taca.label)).thenApply { order ->
          val items = toMap(order)
          val rangeStateDec = state.mealsAgg.getAgg(pos)
          if (rangeStateDec != null) {
            rangeStateDec.orderMeals2Count.decrementAndGet(items)
          }
          val rangeStateInc = state.mealsAgg.getAggOrNew(pos + 1)
          rangeStateInc.orderMeals2Count.incrementAndGet(items)
          null
        }
      }
    }


    // shift border order between neighbour  position from indexes
    //
  }

  private fun addOrderToAllExistingRanges(order: OrderMem, festival: Festival) {
    val items = toMap(order)
    val state = festival.estimatorState
    OrderExecTimeEstimator.indexes.forEach { v ->
      val rangeState = state.mealsAgg.getAgg(v)
      if (rangeState != null) {
        rangeState.orderMeals2Count.incrementAndGet(items)
      }
    }
  }

  fun aggregateOut() {

  }
}