package org.dan.jadalnia.util

import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.completedFuture
import java.util.concurrent.CopyOnWriteArrayList

class Futures {
    companion object {
        @JvmStatic
        fun voidF(): CompletableFuture<Void> = completedFuture(null)

        @JvmStatic
        fun <T> allOf(subFutures: List<CompletableFuture<T>>): CompletableFuture<List<T>> {
            val allSubFuturesReady: CompletableFuture<List<T>> = CompletableFuture()
            val results = CopyOnWriteArrayList<T>()
            allOf(subFutures, allSubFuturesReady, results)
            return allSubFuturesReady

        }

        private fun <T> allOf(
            subFutures: List<CompletableFuture<T>>,
            allSubFuturesReady: CompletableFuture<List<T>>,
            results: MutableList<T>) {
            if (subFutures.isEmpty()) {
                allSubFuturesReady.complete(results);
            } else {
                subFutures.first().whenComplete { v, e ->
                    if (e != null) {
                        allSubFuturesReady.completeExceptionally(e)
                    } else {
                        results.add(v)
                        allOf(subFutures.subList(1, subFutures.size),
                            allSubFuturesReady, results)
                    }
                }
            }
        }
    }
}
