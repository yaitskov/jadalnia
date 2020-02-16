package org.dan.jadalnia.app.token

data class TokenBalanceView(
    val pendingTokens: TokenPoints,
    val effectiveTokens: TokenPoints)
