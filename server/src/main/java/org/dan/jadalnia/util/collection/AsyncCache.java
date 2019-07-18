package org.dan.jadalnia.util.collection;

import com.google.common.cache.LoadingCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dan.jadalnia.sys.error.Error;
import org.dan.jadalnia.sys.error.JadEx;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@RequiredArgsConstructor
public class AsyncCache<K, V> {
    private final LoadingCache<K, CompletableFuture<V>> cache;

    public CompletableFuture<V> get(K key) {
        try {
            return cache.get(key).exceptionally(e -> {
                log.info("Invalidate key {} due exception", key);
                cache.invalidate(key);
                throw new RuntimeException(e);
            });
        } catch (ExecutionException e) {
            throw new JadEx(500,
                    new Error("Failure on resolving key [" + key + "]"), e);
        }
    }

    public void invalidate(K key) {
        cache.invalidate(key);
    }
}
