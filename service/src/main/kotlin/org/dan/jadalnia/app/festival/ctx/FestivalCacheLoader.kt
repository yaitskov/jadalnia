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


class FestivalCacheLoader @Inject constructor(
        val labelDao: LabelDao,
        val orderDao: OrderDao,
        val festivalDao: FestivalDao,
        val orderAggregator: OrderAggregator,
        val wsBroadcast: WsBroadcast) :
        CacheLoader<Fid, CompletableFuture<Festival>>()  {

    override fun load(fid: Fid): CompletableFuture<Festival> {
        return festivalDao.getById(fid).thenCompose({ festInfo ->
            labelDao.maxOrderNumber(fid)
                    .thenCompose({ maxId ->
                        orderDao.loadPaid(fid).thenCompose({ paidOrders ->
                            completedFuture(
                                    Festival(
                                            AtomicReference(festInfo),
                                            paidOrders,
                                            wsBroadcast.busyKelners(fid),
                                            orderAggregator.aggregate(
                                                    paidOrders.values),
                                            AtomicInteger(maxId.getId() + 1)))
                        })
                    })
        })
    }
}
