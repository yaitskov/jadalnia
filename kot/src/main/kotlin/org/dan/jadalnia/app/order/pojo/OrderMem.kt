package org.dan.jadalnia.app.order.pojo

import org.dan.jadalnia.app.user.Uid
import java.util.concurrent.atomic.AtomicReference

data class OrderMem(
        val customer: Uid,
        val state: AtomicReference<OrderState>,
        val label: OrderLabel,
        val items: List<OrderItem>)
