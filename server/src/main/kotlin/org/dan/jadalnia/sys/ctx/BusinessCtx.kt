package org.dan.jadalnia.sys.ctx

import org.dan.jadalnia.app.festival.ctx.FestivalCtx
import org.dan.jadalnia.app.label.OrderLabelCtx
import org.dan.jadalnia.app.order.OrderCtx
import org.dan.jadalnia.app.token.TokenCtx
import org.dan.jadalnia.app.user.UserCtx
import org.springframework.context.annotation.Import

@Import(
    UserCtx::class,
    FestivalCtx::class,
    OrderCtx::class,
    TokenCtx::class,
    OrderLabelCtx::class
)
class BusinessCtx

