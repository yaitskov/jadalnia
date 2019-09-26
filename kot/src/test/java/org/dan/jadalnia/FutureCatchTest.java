package org.dan.jadalnia;

import org.dan.jadalnia.sys.error.Exceptions;
import org.hamcrest.core.Is;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static org.junit.Assert.assertThat;

public class FutureCatchTest {
    volatile Throwable ex;
    @Test
    public void ff() throws Exception {
        assertThat(supplyAsync(() -> 3)
                .thenApply(n -> 10 / (n  - 3))
                .thenCompose(nd -> {
                    System.out.println("boook");
                    return completedFuture(nd);
                })
                .exceptionally(e -> {
                    ex = e;
                    return 88;
                }).get(1L, TimeUnit.MINUTES), Is.is(88));

        assertThat(Exceptions.rootCause(ex).getClass().getSimpleName(), Is.is("ArithmeticException"));
    }
}
