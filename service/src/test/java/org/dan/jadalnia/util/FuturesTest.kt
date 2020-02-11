package org.dan.jadalnia.util

import org.dan.jadalnia.util.Futures.reduce
import org.hamcrest.core.Is
import org.junit.Assert
import org.junit.Test
import java.util.Arrays.asList
import java.util.concurrent.CompletableFuture.completedFuture
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.TimeUnit

class FuturesTest {
  @Test
  fun reduce() {
    check(
        LinkedBlockingDeque(asList("hello", "word")).iterator(),
        9)
  }

  @Test
  fun reduceIteratorAfterEnqueue() {
    val queue = LinkedBlockingDeque(asList("h", "w"))
    val stream = queue.iterator()
    Assert.assertThat(stream.next(), Is.`is`("h"))
    queue.offerLast("xyz")
    queue.offerFirst("aaaaa")
    check(stream, 4)
  }

  @Test
  fun reduceIteratorAfterEmpty() {
    val queue = LinkedBlockingDeque(asList("h", "w", "x"))
    val stream = queue.iterator()
    Assert.assertThat(stream.next(), Is.`is`("h"))
    queue.poll()
    queue.poll()
    queue.poll()
    Assert.assertThat(queue.isEmpty(), Is.`is`(true))
    check(stream, 1) // 1 is left over ;)
  }

  private fun check(stream: MutableIterator<String>, expectedLength: Int) {
    Assert.assertThat(
        reduce<String, Int>(
            { word, sum -> completedFuture(word.length + sum) },
            0,
            stream)
            .get(3, TimeUnit.SECONDS),
        Is.`is`(expectedLength))
  }
}