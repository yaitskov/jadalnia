package org.dan.jadalnia.app.festival.menu

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import org.dan.jadalnia.app.order.pojo.OrderLabel

import org.junit.Test



class OrderLabelTest {
  @Test
  fun marshalingStable() {
    for (i in 0..221) {
      val originLabel = OrderLabel.of(i)
      val textView = originLabel.toString()
      assertThat(i).isEqualTo(OrderLabel(textView).getId())
      assertThat(textView.matches("^[A-Z][0-9]+".toRegex())).isTrue()
    }
  }

  @Test
  fun serialize() = assertThat(OrderLabel("A1").toString()).isEqualTo("A1")
}
