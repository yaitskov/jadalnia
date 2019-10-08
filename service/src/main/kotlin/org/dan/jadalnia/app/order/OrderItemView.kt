package org.dan.jadalnia.app.order

import org.dan.jadalnia.app.order.pojo.OrderLabel
import org.dan.jadalnia.app.order.pojo.OrderState
import java.time.Instant

data class OrderItemView(
    val label: OrderLabel,
    val created: Instant,
    val state: OrderState)
