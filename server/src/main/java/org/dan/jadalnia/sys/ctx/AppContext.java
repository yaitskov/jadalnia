package org.dan.jadalnia.sys.ctx;

import org.dan.jadalnia.app.auth.AuthCtx;
import org.dan.jadalnia.app.festival.FestivalCtx;
import org.dan.jadalnia.app.order.OrderCtx;
import org.dan.jadalnia.app.user.UserCtx;
import org.dan.jadalnia.sys.ctx.jackson.JacksonContext;
import org.dan.jadalnia.sys.db.DbContext;
import org.dan.jadalnia.sys.seqex.SeqexCtx;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Import({PropertiesContext.class,
        AuthCtx.class,
        UserCtx.class,
        FestivalCtx.class,
        OrderCtx.class,
        TimeContext.class,
        JacksonContext.class,
        SeqexCtx.class,
        DbContext.class})
@EnableTransactionManagement(proxyTargetClass = true)
public class AppContext {
}
