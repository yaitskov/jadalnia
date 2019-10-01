package org.dan.jadalnia.app.token

import com.google.common.cache.CacheLoader
import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.order.pojo.OrderLabel
import org.dan.jadalnia.app.order.pojo.OrderMem
import org.dan.jadalnia.app.user.Uid
import org.dan.jadalnia.sys.error.JadEx.Companion.notFound
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

class TokenBalanceCacheLoader @Inject constructor(val tokenDao: TokenDao)
    : CacheLoader<Pair<Fid, Uid>, CompletableFuture<TokenBalance>>() {

    override fun load(key: Pair<Fid, Uid>): CompletableFuture<TokenBalance> {
        return tokenDao.loadBalance(key.first, key.second)
            .thenApply { points -> TokenBalance(AtomicReference(points)) }
    }
}