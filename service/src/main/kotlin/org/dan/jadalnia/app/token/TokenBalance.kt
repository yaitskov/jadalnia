package org.dan.jadalnia.app.token

import java.util.concurrent.atomic.AtomicReference

data class TokenBalance(val balance: AtomicReference<TokenPoints>)
