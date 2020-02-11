package org.dan.jadalnia.app.token

data class TokenStats(
    val boughtByCustomers: TokenPoints,
    val returnedToCustomers: TokenPoints,
    val pendingBoughtByCustomers: TokenPoints,
    val pendingReturnToCustomers: TokenPoints)
