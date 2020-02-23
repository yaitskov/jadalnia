package org.dan.jadalnia.app.order.line

import java.util.concurrent.ConcurrentMap

class QueueRanges(val queueIdx2Agg: ConcurrentMap<Int, QueueRangeState>) {
  fun getAggOrNew(queueIdx: Int): QueueRangeState {
    var result = queueIdx2Agg[queueIdx]
    if (result == null) {
      val newCandidate = QueueRangeState()
      result = queueIdx2Agg.putIfAbsent(queueIdx,  newCandidate)
      if (result == null) {
        result = newCandidate
      }
    }
    return result
  }
}
