package org.dan.jadalnia.app.token

import org.dan.jadalnia.app.user.Uid

data class TokensApproveReq(
    val customer: Uid,
    val tokens: List<TokenId>)
