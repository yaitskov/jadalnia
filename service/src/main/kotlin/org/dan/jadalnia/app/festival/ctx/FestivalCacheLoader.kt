package org.dan.jadalnia.app.festival.ctx

import com.google.common.cache.CacheLoader

import org.dan.jadalnia.app.festival.FestivalDao
import org.dan.jadalnia.app.festival.pojo.Festival
import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.label.LabelDao
import org.dan.jadalnia.app.order.OrderAggregator
import org.dan.jadalnia.app.order.OrderDao
import org.dan.jadalnia.app.ws.WsBroadcast

import javax.inject.Inject
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

import java.util.concurrent.CompletableFuture.completedFuture
import java.util.concurrent.ConcurrentHashMap


class FestivalCacheLoader @Inject constructor(
        val labelDao: LabelDao,
        val orderDao: OrderDao,
        val festivalDao: FestivalDao,
        val orderAggregator: OrderAggregator,
        val wsBroadcast: WsBroadcast) :
        CacheLoader<Fid, CompletableFuture<Festival>>()  {

    override fun load(fid: Fid): CompletableFuture<Festival> {
        return festivalDao.getById(fid).thenCompose { festInfo ->
            labelDao.maxOrderNumber(fid)
                    .thenCompose { maxId ->
                        orderDao.loadReadyToExecOrders(fid)
                                .thenCompose { readyToExecOrders ->
                                    completedFuture(
                                            Festival(
                                                    info = AtomicReference(festInfo),
                                                    readyToExecOrders = readyToExecOrders,
                                                    freeKelners = ConcurrentHashMap(),
                                                    nextLabel = AtomicInteger(maxId.getId() + 1)))
                                }
                    }
        }
    }
}
