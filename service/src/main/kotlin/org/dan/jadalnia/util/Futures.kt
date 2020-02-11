package org.dan.jadalnia.util

import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.completedFuture
import java.util.concurrent.CopyOnWriteArrayList

object Futures {
    fun voidF(): CompletableFuture<Void> = completedFuture(null)

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

    fun <T, B> reduce(
        reductor: Function2<T, B, CompletableFuture<B>>,
        base: B, stream: Iterator<T>): CompletableFuture<B> {
        if (stream.hasNext()) {
            return reductor(stream.next(), base).thenCompose {
                newBase -> reduce(reductor, newBase, stream)
            }
        } else {
            return completedFuture(base)
        }
    }
}
