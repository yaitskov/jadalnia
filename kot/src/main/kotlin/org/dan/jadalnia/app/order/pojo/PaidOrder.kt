package org.dan.jadalnia.app.order.pojo;

data class PaidOrder(
        val orderNumber: Oid,
        val orderLabel: OrderLabel,
        val items: MutableList<OrderItem>)

