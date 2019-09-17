package org.dan.jadalnia.util

import java.util.concurrent.CompletableFuture

class Futures {
    companion object {
        @JvmStatic
        fun voidF(): CompletableFuture<Void> = CompletableFuture.completedFuture(null)
    }
}
