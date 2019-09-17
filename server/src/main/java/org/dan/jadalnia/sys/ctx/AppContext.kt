package org.dan.jadalnia.sys.ctx

import org.dan.jadalnia.app.auth.ctx.AuthCtx
import org.dan.jadalnia.app.ws.WsCtx
import org.dan.jadalnia.sys.ctx.jackson.JacksonContext
import org.dan.jadalnia.sys.db.DbContext
import org.springframework.context.annotation.Import
import org.springframework.transaction.annotation.EnableTransactionManagement

@Import(
    PropertiesContext::class,
    AuthCtx::class,
    WsCtx::class,
    BusinessCtx::class,
    TimeContext::class,
    ExecutorCtx::class,
    AsyncCtx::class,
    JacksonContext::class,
    DbContext::class
)
@EnableTransactionManagement(proxyTargetClass = true)
class AppContext

