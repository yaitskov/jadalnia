package org.dan.jadalnia.app.token

import java.util.concurrent.atomic.AtomicReference

data class TokenBalance(
    val effective: AtomicReference<TokenPoints>,
    val pending: AtomicReference<TokenPoints>)
