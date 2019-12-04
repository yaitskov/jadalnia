package org.dan.jadalnia.app.order

import org.dan.jadalnia.app.order.pojo.OrderLabel
import org.dan.jadalnia.app.order.pojo.OrderState
import org.dan.jadalnia.app.token.TokenPoints

data class VisitorOrderView (
    val label: OrderLabel,
    val price: TokenPoints,
    val state: OrderState)
