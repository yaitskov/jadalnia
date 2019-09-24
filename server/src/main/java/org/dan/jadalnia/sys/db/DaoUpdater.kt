package org.dan.jadalnia.sys.db

import java.util.concurrent.CompletableFuture

class DaoUpdater {
    fun <T> exec(futureFactory: () -> CompletableFuture<T>): CompletableFuture<T> {
        return futureFactory()
    }
}