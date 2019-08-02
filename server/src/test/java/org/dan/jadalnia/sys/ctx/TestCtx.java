package org.dan.jadalnia.sys.ctx;

import org.dan.jadalnia.app.auth.ctx.AuthCtx;
import org.dan.jadalnia.app.ws.WsCtx;
import org.dan.jadalnia.sys.ctx.jackson.JacksonContext;
import org.dan.jadalnia.sys.db.DbContext;
import org.springframework.context.annotation.Import;

@Import({PropertiesContext.class, TimeContext.class, DbContext.class,
        ExecutorCtx.class,
        BusinessCtx.class, WsCtx.class,
        JacksonContext.class, AuthCtx.class,
        JerseyCtx.class })
public class TestCtx {
}
