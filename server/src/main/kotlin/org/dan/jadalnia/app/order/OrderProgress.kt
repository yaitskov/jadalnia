package org.dan.jadalnia.app.order

import org.dan.jadalnia.app.order.pojo.OrderState

data class OrderProgress(
    val ordersAhead: Int,
    val etaSeconds: Int,
    val state: OrderState) {
}