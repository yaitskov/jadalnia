package org.dan.jadalnia.app.order.line

import org.dan.jadalnia.app.festival.pojo.Festival
import org.dan.jadalnia.app.user.Uid
import org.dan.jadalnia.sys.error.JadEx.Companion.internalError

class OrderExecTimeEstimator {
  companion object {
    val indexes = intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 20, 40, 80,
        100, 200, 500, 1000)
  }

  fun estimateFor(festival: Festival, queuePosition: Int, activeKelners: Set<Uid>)
      : OrderExecEstimate {
    val idxAfter = findQueueIdxAfterWithEstimate(queuePosition)
    if (idxAfter < 0) { // too far
      return approximateByLeftOnly(indexes.last(), queuePosition,
          festival.estimatorState, activeKelners)
    } else if (indexes[idxAfter] == queuePosition) { // exact match
      return exactMatch(queuePosition, festival.estimatorState, activeKelners)
    } else {
      if (0 == idxAfter) {
        throw internalError("idx = 0")
      } else {
        val leftIdx = indexes[idxAfter - 1]
        return approximateBetween(leftIdx, indexes[idxAfter], queuePosition,
            festival.estimatorState, activeKelners)
      }
    }
  }

  // idx >> than biggest idx with exact estimate
  private fun approximateByLeftOnly(
      biggestIdx: Int, queuePosition: Int,
      estimatorState: OrderExecEstimationState,
      activeKelners: Set<Uid>)
      : OrderExecEstimate {
    val ex = exactMatch(biggestIdx, estimatorState, activeKelners)
    return OrderExecEstimate(
        (ex.minutes.toDouble() * queuePosition /
            biggestIdx.toDouble()).toInt())
  }

  private fun exactMatch(
      queueIdx: Int,
      estimatorState: OrderExecEstimationState,
      activeKelners: Set<Uid>) = estimatorState.estimate(activeKelners, queueIdx)

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
      activeKelners: Set<Uid>)
      : OrderExecEstimate {
    val leftEstimate = exactMatch(idxWithEstimateFromLeft,
        estimatorState, activeKelners)
    val rightEstimate = exactMatch(idxWithEstimateFromRight,
        estimatorState, activeKelners)

    val betweenLeftAndRight = (idxWithEstimateFromRight - idxWithEstimateFromLeft).toDouble()
    val diffEstimate = (rightEstimate.minutes - leftEstimate.minutes).toDouble()
    val toLeft = (idx - idxWithEstimateFromLeft).toDouble()
    return OrderExecEstimate((toLeft *  diffEstimate / betweenLeftAndRight).toInt())
  }
}