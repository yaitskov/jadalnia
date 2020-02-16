package org.dan.jadalnia.app.token

import org.dan.jadalnia.app.user.Uid
import java.time.Instant

data class TokenRequestCashierView(
    val tokenId: TokenId,
    val amount: TokenPoints,
    val approvedAt: Instant?,
    val cancelledBy: TokenId?,
    val customer: Uid)
