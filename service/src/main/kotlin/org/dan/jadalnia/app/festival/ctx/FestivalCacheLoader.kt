package org.dan.jadalnia.app.festival.ctx

import com.google.common.cache.CacheLoader

import org.dan.jadalnia.app.festival.FestivalDao
import org.dan.jadalnia.app.festival.pojo.Festival
import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.label.LabelDao
import org.dan.jadalnia.app.order.OrderAggregator
import org.dan.jadalnia.app.order.OrderDao
import org.dan.jadalnia.app.token.TokenDao
import org.dan.jadalnia.app.ws.WsBroadcast

import javax.inject.Inject
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

import java.util.concurrent.CompletableFuture.completedFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.LinkedBlockingDeque

class FestivalCacheLoader @Inject constructor(
    val labelDao: LabelDao,
    val orderDao: OrderDao,
    val tokenDao: TokenDao,
    val festivalDao: FestivalDao,
    val orderAggregator: OrderAggregator,
    val wsBroadcast: WsBroadcast) :
    CacheLoader<Fid, CompletableFuture<Festival>>()  {

  override fun load(fid: Fid): CompletableFuture<Festival> {
    return festivalDao.getById(fid).thenCompose { festInfo ->
      labelDao.maxOrderNumber(fid)
          .thenCompose { maxLabelId ->
            tokenDao.maxTokenNumber(fid).thenCompose { maxTokenId ->
              orderDao.loadReadyToExecOrders(fid)
                  .thenCompose { readyToExecOrders ->
                    completedFuture(
                        Festival(
                            info = AtomicReference(festInfo),
                            readyToExecOrders = LinkedBlockingDeque(readyToExecOrders),
                            readyToPickupOrders = ConcurrentHashMap(),
                            busyKelners = ConcurrentHashMap(),
                            freeKelners = ConcurrentHashMap(),
                            executingOrders = ConcurrentHashMap(), // load from db
                            nextToken = AtomicInteger(maxTokenId.value + 1),
                            nextLabel = AtomicInteger(maxLabelId.getId() + 1)))
                  }
            }
          }
    }
  }
}
