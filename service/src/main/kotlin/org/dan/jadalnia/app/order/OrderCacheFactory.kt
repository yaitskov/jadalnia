package org.dan.jadalnia.app.order

import com.google.common.cache.CacheBuilder
import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.order.pojo.OrderLabel
import org.dan.jadalnia.app.order.pojo.OrderMem
import org.dan.jadalnia.util.collection.AsyncCache
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class OrderCacheFactory @Inject constructor(val loader: OrderCacheLoader) {
    companion object {
        val log = LoggerFactory.getLogger(OrderCacheFactory::class.java)
    }

    @Bean
    fun create(): AsyncCache<Pair<Fid, OrderLabel>, OrderMem> {
        return AsyncCache(
                CacheBuilder
                        .newBuilder()
                        .expireAfterAccess(100, TimeUnit.MINUTES)
                        .build(loader))
    }
}