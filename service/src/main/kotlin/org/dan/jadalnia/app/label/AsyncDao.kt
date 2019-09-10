package org.dan.jadalnia.app.label;

import org.dan.jadalnia.sys.ctx.FutureExecutor;
import org.jooq.DSLContext;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;


open class AsyncDao {
    @Inject
    protected var jooq: DSLContext? = null
    @Inject
    protected var executor: FutureExecutor? = null

    protected fun <T> execQuery(query: (DSLContext) -> T): CompletableFuture<T> {
        return (executor as FutureExecutor)
                .run({ query(jooq as DSLContext) });
    }
}
