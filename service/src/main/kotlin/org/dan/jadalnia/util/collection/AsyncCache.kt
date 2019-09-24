package org.dan.jadalnia.util.collection

import com.google.common.cache.LoadingCache
import org.dan.jadalnia.sys.error.Error
import org.dan.jadalnia.sys.error.JadEx
import org.slf4j.LoggerFactory

import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.completedFuture
import java.util.concurrent.ExecutionException

class AsyncCache<K, V>(val cache: LoadingCache<K, CompletableFuture<V>>) {
    companion object {
        val log = LoggerFactory.getLogger(AsyncCache::class.java)
    }

    fun get(key: K): CompletableFuture<V> {
        try {
            return cache.get(key).exceptionally { e ->
                log.info("Invalidate key {} due exception", key);
                cache.invalidate(key);
                throw RuntimeException(e);
            }
        } catch (e: ExecutionException) {
            throw JadEx(500, Error("Failure on resolving key [$key]"), e);
        }
    }

    fun invalidate(k: K) = cache.invalidate(k)

    fun inject(key: K, value: V): V {
        cache.put(key, completedFuture(value))
        return value
    }
}
