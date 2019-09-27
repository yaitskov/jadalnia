package org.dan.jadalnia.app.order

import org.dan.jadalnia.app.order.pojo.OrderLabel
import org.dan.jadalnia.app.order.pojo.OrderState
import org.dan.jadalnia.app.ws.MessageForClient

data class OrderStateEvent(
    val label: OrderLabel, val state: OrderState): MessageForClient
