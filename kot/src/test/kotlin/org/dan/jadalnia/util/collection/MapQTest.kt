package org.dan.jadalnia.util.collection

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import org.dan.jadalnia.util.collection.MapQ.QueueInsertIdx
import org.junit.Test
import java.util.concurrent.ConcurrentHashMap

class MapQTest {
  @Test
  fun isEmpty() {
    assertThat(emptyMapQ().count()).isEqualTo(0)
    assertThat(emptyMapQ().isEmpty()).isEqualTo(true)
  }

  fun emptyMapQ() = MapQ(ConcurrentHashMap<QueueInsertIdx, Int>())

  @Test
  fun isNotEmpty() =
    assertThat(MapQ(ConcurrentHashMap(mapOf(Pair(QueueInsertIdx(1), 2)))).isEmpty())
        .isEqualTo(false)

  @Test
  fun iterates() {
    assertThat(
        MapQ(ConcurrentHashMap(
            mapOf(Pair(QueueInsertIdx(1), 2)))).iterator().asSequence().toList())
        .isEqualTo(listOf(2))
  }

  @Test
  fun pollFromReconstructed() {
    val m = MapQ(ConcurrentHashMap(
        mapOf(Pair(QueueInsertIdx(1), 4))))
    assertThat(m.isEmpty()).isEqualTo(false)
    assertThat(m.count()).isEqualTo(1)
    assertThat(m.poll()?.second).isEqualTo(4)
    assertThat(m.count()).isEqualTo(0)
    assertThat(m.isEmpty()).isEqualTo(true)
    assertThat(m.poll()?.second).isNull()
  }

  @Test
  fun enqueuePositionByIdxAndPoll() {
    val m = emptyMapQ()
    val idx = m.enqueue(7)
    val idx2 = m.enqueue(4)
    assertThat(idx.inc()).isEqualTo(idx2)
    assertThat(m.count()).isEqualTo(2)
    assertThat(m.positionByIdx(idx)).isEqualTo(0)
    assertThat(m.positionByIdx(idx2)).isEqualTo(1)
    assertThat(m.poll()?.second).isEqualTo(7)
    assertThat(m.positionByIdx(idx2)).isEqualTo(0)
    assertThat(m.poll()?.second).isEqualTo(4)
    assertThat(m.poll()?.second).isNull()
    assertThat(m.count()).isEqualTo(0)
  }

  @Test
  fun enqueueHeadPositionByIdxAndPoll() {
    val m = emptyMapQ()
    val idx = m.enqueueHead(7)
    val idx2 = m.enqueueHead(4)
    assertThat(idx2.inc()).isEqualTo(idx)
    assertThat(m.count()).isEqualTo(2)
    assertThat(m.positionByIdx(idx2)).isEqualTo(0)
    assertThat(m.positionByIdx(idx)).isEqualTo(1)
    assertThat(m.poll()?.second).isEqualTo(4)
    assertThat(m.positionByIdx(idx)).isEqualTo(0)
    assertThat(m.poll()?.second).isEqualTo(7)
    assertThat(m.poll()?.second).isNull()
    assertThat(m.count()).isEqualTo(0)
  }
}