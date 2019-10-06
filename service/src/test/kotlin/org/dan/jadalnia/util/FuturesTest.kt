package org.dan.jadalnia.util



import assertk.assertThat
import assertk.assertions.isEqualTo
import org.dan.jadalnia.util.Futures.Companion.allOf
import org.junit.Test
import java.util.concurrent.CompletableFuture.completedFuture
import java.util.concurrent.TimeUnit

class FuturesTest {
  @Test
  fun `allOf 1 completes`() {
    assertThat(
        allOf(listOf(completedFuture(3)))
            .get(10, TimeUnit.SECONDS)).isEqualTo(listOf(3))
  }
}