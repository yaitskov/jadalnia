package org.dan.jadalnia.sys.ctx;

import org.dan.jadalnia.app.auth.ctx.AuthCtx;
import org.dan.jadalnia.app.festival.ctx.FestivalCtx;
import org.dan.jadalnia.app.festival.order.OrderCtx;
import org.dan.jadalnia.app.user.UserCtx;
import org.dan.jadalnia.sys.ctx.jackson.JacksonContext;
import org.dan.jadalnia.sys.db.DbContext;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Import({PropertiesContext.class,
        AuthCtx.class,
        UserCtx.class,
        FestivalCtx.class,
        OrderCtx.class,
        TimeContext.class,
        JacksonContext.class,
        DbContext.class})
@EnableTransactionManagement(proxyTargetClass = true)
public class AppContext {
}
