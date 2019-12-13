package org.dan.jadalnia.app.token

data class TokenRequestVisitorView(
    val tokenRequestId: TokenId,
    val amount: TokenPoints,
    val approved: Boolean)
