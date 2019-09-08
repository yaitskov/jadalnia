package org.dan.jadalnia.util;

import java.util.concurrent.CompletableFuture;

public class Futures {
    public static CompletableFuture<Void> voidF() {
        return CompletableFuture.completedFuture(null);
    }
}
