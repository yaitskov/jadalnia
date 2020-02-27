package org.dan.jadalnia.app.order.line

import org.dan.jadalnia.app.festival.pojo.FestParams
import org.dan.jadalnia.app.festival.pojo.Festival
import org.dan.jadalnia.app.user.Uid

class OrderExecTimeEstimator {
  companion object {
    val indexes = intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 20, 40, 80,
        100, 200, 500, 1000)
  }

  fun estimateFor(festival: Festival, queuePosition: Int,
                  activeKelners: Set<Uid>, params: FestParams)
      : OrderExecEstimate {
    val idxAfter = findQueueIdxAfterWithEstimate(queuePosition)
    if (idxAfter < 0) { // too far
      return approximateByLeftOnly(indexes.last(), queuePosition,
          festival.estimatorState, activeKelners, params)
    } else if (indexes[idxAfter] == queuePosition) { // exact match
      return exactMatch(queuePosition, festival.estimatorState,
          activeKelners, params)
    } else {
      if (0 == idxAfter) {
        return OrderExecEstimate(0)
      } else {
        val leftIdx = indexes[idxAfter - 1]
        return approximateBetween(leftIdx, indexes[idxAfter], queuePosition,
            festival.estimatorState, activeKelners, params)
      }
    }
  }

  // idx >> than biggest idx with exact estimate
  private fun approximateByLeftOnly(
      biggestIdx: Int, queuePosition: Int,
      estimatorState: OrderExecEstimationState,
      activeKelners: Set<Uid>,
      params: FestParams)
      : OrderExecEstimate {
    val ex = exactMatch(biggestIdx, estimatorState, activeKelners, params)
    return OrderExecEstimate(
        (ex.minutes.toDouble() * queuePosition /
            biggestIdx.toDouble()).toInt())
  }

  private fun exactMatch(
      queueIdx: Int,
      estimatorState: OrderExecEstimationState,
      activeKelners: Set<Uid>,
      params: FestParams)
      = estimatorState.estimate(activeKelners, queueIdx, params)

  private fun findQueueIdxAfterWithEstimate(queuePosition: Int): Int {
    val v = indexes.binarySearch(queuePosition)
    if (v < 0) {
      val idx = -v - 1
      if (idx == indexes.size) {
        return -1
      }
      return idx
    }
    return v
  }

  private fun approximateBetween(
      idxWithEstimateFromLeft: Int,
      idxWithEstimateFromRight: Int,
      idx: Int,
      estimatorState: OrderExecEstimationState,
      activeKelners: Set<Uid>,
      params: FestParams)
      : OrderExecEstimate {
    val leftEstimate = exactMatch(idxWithEstimateFromLeft,
        estimatorState, activeKelners, params)
    val rightEstimate = exactMatch(idxWithEstimateFromRight,
        estimatorState, activeKelners, params)

    val betweenLeftAndRight = (idxWithEstimateFromRight - idxWithEstimateFromLeft).toDouble()
    val diffEstimate = (rightEstimate.minutes - leftEstimate.minutes).toDouble()
    val toLeft = (idx - idxWithEstimateFromLeft).toDouble()
    return OrderExecEstimate((toLeft *  diffEstimate / betweenLeftAndRight).toInt())
  }
}