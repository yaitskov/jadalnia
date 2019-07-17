package org.dan.jadalnia.app.festival;

import static java.util.concurrent.TimeUnit.SECONDS;

import com.google.common.cache.CacheBuilder;
import org.dan.jadalnia.util.collection.AsyncCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import javax.inject.Inject;

public class FestivalCacheFactory {
    @Inject
    private FestivalCacheLoader loader;

    @Value("${expire.festival.seconds}")
    private int expireFestivalSeconds;

    @Bean(name = FestivalCache.FESTIVAL_CACHE)
    public AsyncCache<Fid, Festival> create() {
        return new AsyncCache<>(
                CacheBuilder.newBuilder()
                        .expireAfterAccess(expireFestivalSeconds, SECONDS)
                        .build(loader));
    }
}
