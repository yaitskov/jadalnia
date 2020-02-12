package org.dan.jadalnia.app.order.pref

import org.dan.jadalnia.app.token.TokenPoints

data class KelnerPerformanceRow(
    val name: String,
    val orders: Int,
    val tokens: TokenPoints)