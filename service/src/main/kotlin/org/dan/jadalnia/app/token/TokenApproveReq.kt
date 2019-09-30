package org.dan.jadalnia.app.token

import org.dan.jadalnia.app.user.Uid

data class TokenApproveReq(
    val customer: Uid,
    val amount: TokenPoints)
