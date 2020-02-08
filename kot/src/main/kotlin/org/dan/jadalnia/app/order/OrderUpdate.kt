package org.dan.jadalnia.app.order

import org.dan.jadalnia.app.order.pojo.OrderItem
import org.dan.jadalnia.app.order.pojo.OrderLabel

data class OrderUpdate(
    val label: OrderLabel,
    val newItems: List<OrderItem>)
