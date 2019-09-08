package org.dan.jadalnia.app.label;

import org.jooq.DSLContext;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.dan.jadalnia.sys.ctx.ExecutorCtx.DEFAULT_EXECUTOR;

public class AsyncDao {
    @Inject
    private DSLContext jooq;
    @Inject
    @Named(DEFAULT_EXECUTOR)
    private ExecutorService executor;

    protected <T> CompletableFuture<T> execQuery(Function<DSLContext, T> query) {
        return supplyAsync(() -> query.apply(jooq), executor);
    }
}
