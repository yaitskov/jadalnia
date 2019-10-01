package org.dan.jadalnia.app.token

import com.google.common.cache.CacheBuilder
import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.user.Uid
import org.dan.jadalnia.util.collection.AsyncCache
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TokenBalanceCacheFactory @Inject constructor(val loader: TokenBalanceCacheLoader) {
    companion object {
        val log = LoggerFactory.getLogger(TokenBalanceCacheFactory::class.java)
    }

    @Bean(name = ["tokenBalanceCache"])
    fun create(): AsyncCache<Pair<Fid, Uid>, TokenBalance> {
        return AsyncCache(
                CacheBuilder
                        .newBuilder()
                        .expireAfterAccess(100, TimeUnit.MINUTES)
                        .build(loader))
    }
}