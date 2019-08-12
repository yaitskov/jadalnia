package org.dan.jadalnia.app.festival.ctx;

import static java.util.concurrent.TimeUnit.SECONDS;

import com.google.common.cache.CacheBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dan.jadalnia.app.festival.pojo.Festival;
import org.dan.jadalnia.app.festival.pojo.Fid;
import org.dan.jadalnia.util.collection.AsyncCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import javax.inject.Inject;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class FestivalCacheFactory {
    public static final String FESTIVAL_CACHE = "festival-cache";

    private final FestivalCacheLoader loader;

    @Value("${expire.festival.seconds}")
    private final int expireFestivalSeconds;

    @Bean(name = FESTIVAL_CACHE)
    public AsyncCache<Fid, Festival> create() {
        return new AsyncCache<>(
                CacheBuilder.newBuilder()
                        .expireAfterAccess(expireFestivalSeconds, SECONDS)
                        .removalListener(notification ->
                                log.info("Evicted festival {}", notification.getKey()))
                        .build(loader));
    }
}
