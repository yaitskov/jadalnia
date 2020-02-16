package org.dan.jadalnia.app.order.pref

import org.dan.jadalnia.app.token.TokenPoints

data class CashierPerformanceRow(
    val name: String,
    val requests: Int,
    val tokens: TokenPoints)