package org.dan.jadalnia.app.order

import org.dan.jadalnia.app.order.pojo.OrderLabel
import org.dan.jadalnia.app.ws.MessageForClient

data class OrderExecutingEvent(val label: OrderLabel): MessageForClient