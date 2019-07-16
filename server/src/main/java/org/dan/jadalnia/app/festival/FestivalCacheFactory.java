package org.dan.jadalnia.app.festival;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class FestivalCacheFactory {
    @Inject
    private FestivalCacheLoader loader;

    @Value("${expire.festival.seconds}")
    private int expireTournamentSeconds;

    @Bean(name = FestivalCache.FESTIVAL_CACHE)
    public LoadingCache<Fid, CompletableFuture<Festival>> create() {
        return CacheBuilder.newBuilder()
                .expireAfterAccess(expireTournamentSeconds, TimeUnit.SECONDS)
                .build(loader);
    }
}
