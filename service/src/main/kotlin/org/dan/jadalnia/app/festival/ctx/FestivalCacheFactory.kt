package org.dan.jadalnia.app.festival.ctx

import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

import com.google.common.cache.CacheBuilder

import org.dan.jadalnia.app.festival.pojo.Festival
import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.util.collection.AsyncCache
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean

import javax.inject.Inject

class FestivalCacheFactory @Inject constructor(
        val loader: FestivalCacheLoader,
        @Value("\${expire.festival.seconds}")
        val expireFestivalSeconds: Long) {

    companion object {
        const val FESTIVAL_CACHE = "festival-cache"
        val log = LoggerFactory.getLogger(FestivalCacheFactory::class.java)
    }

    @Bean(name = [FESTIVAL_CACHE])
    fun create(): AsyncCache<Fid, Festival> {
        return AsyncCache(CacheBuilder.newBuilder()
                .expireAfterAccess(expireFestivalSeconds, TimeUnit.SECONDS)
                .removalListener<Fid, CompletableFuture<Festival>>({
                    notification -> log.info("Evicted festival {}", notification.key)
                })
                .build(loader)
        )
    }
}
