package org.dan.jadalnia.util.collection

import org.dan.jadalnia.sys.error.JadEx.Companion.internalError
import org.dan.jadalnia.sys.type.number.ImmutableNumber
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.atomic.AtomicReference

data class MapQ<Q> private constructor(
  private val map: ConcurrentMap<QueueInsertIdx, Q>,
  private val queueState: AtomicReference<MapQueueState>) {

  companion object {
    fun <Q> limits(map: ConcurrentMap<QueueInsertIdx, Q>)
        : Pair<QueueInsertIdx?, QueueInsertIdx?> {
      return map.keys.fold(
          Pair<QueueInsertIdx?, QueueInsertIdx?>(null, null),
          { acc, idx -> acc.copy(
              first = if (acc.first == null) idx else minOf(acc.first!!, idx),
              second = if (acc.second == null) idx else maxOf(acc.second!!, idx))
          })
    }

    fun queueState(limits: Pair<QueueInsertIdx?, QueueInsertIdx?>)
        : AtomicReference<MapQueueState> {
      return AtomicReference(
          MapQueueState(
              (limits.second ?: QueueInsertIdx(-1)).inc(),
              limits.first ?: QueueInsertIdx(0)))
    }
  }

  constructor(map: ConcurrentMap<QueueInsertIdx, Q>)
      : this(map, queueState(limits(map)))

  class QueueInsertIdx(id: Int): ImmutableNumber(id) {
    fun distance(idxO: QueueInsertIdx) = value - idxO.value
    fun inc() = QueueInsertIdx(value + 1)
    fun dec() = QueueInsertIdx(value - 1)
    fun plus(n: Int) = QueueInsertIdx(value + n)
  }

  data class MapQueueState(
      val nextIndex: QueueInsertIdx,
      val firstIndex: QueueInsertIdx) {
    fun isEmpty() = nextIndex == firstIndex
    fun count() = nextIndex.distance(firstIndex)
  }

  fun enqueue(q: Q): QueueInsertIdx {
    val newSt = queueState.getAndUpdate { st ->
      st.copy(nextIndex = st.nextIndex.inc())
    }
    map[newSt.nextIndex] = q
    return newSt.nextIndex
  }

  fun positionByIdx(insertIdx: QueueInsertIdx): Int {
    return insertIdx.distance(queueState.get().firstIndex)
  }

  operator fun get(idx: QueueInsertIdx): Q? = map[idx]

  fun enqueueHead(q: Q): QueueInsertIdx {
    val newSt = queueState.updateAndGet { st ->
      st.copy(firstIndex = st.firstIndex.dec())
    }
    map[newSt.firstIndex] = q
    return newSt.firstIndex
  }

  fun poll(): Pair<QueueInsertIdx, Q>? {
    val oldSt = queueState.getAndUpdate { st ->
      if (st.isEmpty())
        st
      else
        st.copy(firstIndex = st.firstIndex.inc())
    }
    if (oldSt.isEmpty())
      return null
    return Pair(
        oldSt.firstIndex,
        map.remove(oldSt.firstIndex)
            ?: throw internalError("now key ${oldSt.firstIndex}"))
  }

  fun iterator() = map.values.iterator()

  fun isEmpty() = queueState.get().isEmpty()
  fun count() = queueState.get().count()
}