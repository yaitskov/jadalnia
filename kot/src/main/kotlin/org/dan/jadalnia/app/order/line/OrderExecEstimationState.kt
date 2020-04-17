package org.dan.jadalnia.app.order.line

import org.dan.jadalnia.app.festival.menu.DishName
import org.dan.jadalnia.app.festival.pojo.FestParams
import org.dan.jadalnia.app.user.Uid
import org.dan.jadalnia.sys.error.JadEx.Companion.internalError
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

class OrderExecEstimationState(
    var defaultAvgDishTimeMs: Int,
    val defaultDishTimeMs: ConcurrentMap<DishName, Int>,
    val orderKelner2AvgExecMs: ConcurrentMap<
        Pair<Map<DishName, Int>, Optional<Uid>>, Int>) {

  companion object {
    val defaultAvgDishTimeMs = 60_000
    val emptyOrder = emptyMap<DishName, Int>()
    val defaultOrderBookKeepTimeKey =
        Pair<Map<DishName, Int>, Optional<Uid>>(emptyOrder, Optional.empty())

    fun create(avgOrderBookKeepTimeMs: Int): OrderExecEstimationState {
      return create(avgOrderBookKeepTimeMs, defaultAvgDishTimeMs)
    }

    fun create(avgOrderBookKeepTimeMs: Int, defaultAvgDishTimeMs: Int)
        : OrderExecEstimationState {
      val mealKelnerAvgMs = ConcurrentHashMap<Pair<Map<DishName, Int>, Optional<Uid>>, Int>()
      mealKelnerAvgMs[defaultOrderBookKeepTimeKey] = avgOrderBookKeepTimeMs
      return OrderExecEstimationState(
          defaultAvgDishTimeMs = defaultAvgDishTimeMs,
          defaultDishTimeMs = ConcurrentHashMap(),
          orderKelner2AvgExecMs = mealKelnerAvgMs)
    }
  }

  fun orderReady(orderContent: Map<DishName, Int>, kelner: Uid, durationMs: Int) {
    orderReady(orderContent, Optional.of(kelner), durationMs)
    orderReady(orderContent, Optional.empty(), durationMs)
    orderReady(emptyMap(), Optional.empty(), durationMs)
  }

  private fun orderReady(orderContent: Map<DishName, Int>, kelnerO: Optional<Uid>, durationMs: Int) {
    orderKelner2AvgExecMs.compute(Pair(orderContent, kelnerO))
        { _, msO ->
          if (msO == null) {
            durationMs
          } else {
            (durationMs + msO) / 2
          }
        }
  }

  fun estimate(activeKelners: Set<Uid>, params: FestParams,
               aggDemand: Map<Map<DishName, Int>, Int>)
      : OrderExecEstimate {

    var sumTimeMs = 0.0
    aggDemand.forEach { (orderMeals, count) ->
      sumTimeMs += (params.manualAdjustPerOrderMs + estimateOrderMs(orderMeals, activeKelners)) * count
    }
    return OrderExecEstimate(msToMinutes(sumTimeMs.toLong()))
  }

  fun msToMinutes(ms: Long) = (ms / 1000 / 60).toInt()

  fun findRelativePerformance(absExecTimesMs: Map<Uid, Int>): Map<Uid, Double> {
    val totalMs = absExecTimesMs.entries.fold(0, { s, e -> s + e.value })
        .toDouble()
    return absExecTimesMs.mapValues { e -> e.value / totalMs}
  }

  fun findKelnerPerformance(
      orderMeals: Map<DishName, Int>,
      activeKelners: Set<Uid>): Map<Uid, Int> =
      activeKelners.associate { uid ->
        val exactO = orderKelner2AvgExecMs[Pair(orderMeals, Optional.of(uid))]
        if (exactO != null) {
          Pair(uid, exactO)
        } else {
          val aveO = orderKelner2AvgExecMs[Pair(orderMeals, Optional.empty())]
          if (aveO != null) {
            Pair(uid, aveO)
          } else {
            Pair(uid, estimateByMealsMs(uid, orderMeals))
          }
        }
      }

  fun estimateByMealsMs(uid: Uid, orderMeals: Map<DishName, Int>): Int {
    val orderKeepingBase =
        orderKelner2AvgExecMs[Pair(emptyOrder, Optional.of(uid))] ?:
            orderKelner2AvgExecMs[defaultOrderBookKeepTimeKey]
        ?: throw internalError("defaultOrderBookKeepTimeKey is not set")

    val mealsDuration = orderMeals
        .map { e -> e.value * (defaultDishTimeMs[e.key] ?: defaultAvgDishTimeMs) }
        .foldRight(0, { d, s -> s + d })
    return orderKeepingBase + mealsDuration
  }

  fun estimateOrderMs(orderMeals: Map<DishName, Int>, activeKelners: Set<Uid>)
      : Double {
    val absTimeMs = findKelnerPerformance(orderMeals, activeKelners)
    val performanceByUid = findRelativePerformance(absTimeMs)
    return performanceByUid.map { (uid, kRatio) ->
      absTimeMs[uid]!! * kRatio
    }.sum()
  }
}