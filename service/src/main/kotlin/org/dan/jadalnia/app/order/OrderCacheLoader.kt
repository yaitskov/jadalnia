package org.dan.jadalnia.app.order

import com.google.common.cache.CacheLoader
import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.order.pojo.OrderLabel
import org.dan.jadalnia.app.order.pojo.OrderMem
import org.dan.jadalnia.sys.error.JadEx.Companion.notFound
import java.util.concurrent.CompletableFuture
import javax.inject.Inject

class OrderCacheLoader @Inject constructor(val orderDao: OrderDao)
    : CacheLoader<Pair<Fid, OrderLabel>, CompletableFuture<OrderMem>>() {

    override fun load(key: Pair<Fid, OrderLabel>): CompletableFuture<OrderMem> {
        return orderDao.load(key.first, key.second)
                .thenApply { orderO ->
                    orderO.orElseThrow {
                        notFound("order not found", "order", key.second)
                    }
                }
    }
}