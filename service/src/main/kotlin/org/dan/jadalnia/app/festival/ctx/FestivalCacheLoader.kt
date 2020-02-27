package org.dan.jadalnia.app.festival.ctx

import com.google.common.cache.CacheLoader
import org.dan.jadalnia.app.festival.FestivalDao
import org.dan.jadalnia.app.festival.pojo.Festival
import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.festival.pojo.MapOfQueues
import org.dan.jadalnia.app.festival.pojo.Taca
import org.dan.jadalnia.app.label.LabelDao
import org.dan.jadalnia.app.order.DelayedOrderDao
import org.dan.jadalnia.app.order.OrderDao
import org.dan.jadalnia.app.order.line.OrderExecEstimationState
import org.dan.jadalnia.app.token.TokenDao
import org.dan.jadalnia.app.user.Uid
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.completedFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

class FestivalCacheLoader @Inject constructor(
    val labelDao: LabelDao,
    val orderDao: OrderDao,
    val tokenDao: TokenDao,
    val festivalDao: FestivalDao,
    val delayedOrderDao: DelayedOrderDao) :
    CacheLoader<Fid, CompletableFuture<Festival>>()  {

  override fun load(fid: Fid): CompletableFuture<Festival> {
    return festivalDao.getById(fid).thenCompose { festInfo ->
      labelDao.maxOrderNumber(fid)
          .thenCompose { maxLabelId ->
            tokenDao.maxTokenNumber(fid).thenCompose { maxTokenId ->
              orderDao.loadReadyToExecOrders(fid).thenCompose { readyToExecOrders ->
                orderDao.loadReadyOrders(fid).thenCompose { readies ->
                  orderDao.loadExecutingOrders(fid).thenCompose { orderKelnerId ->
                    delayedOrderDao.load(fid).thenCompose { dish2Orders ->
                      completedFuture(
                          Festival(
                              info = AtomicReference(festInfo),
                              readyToExecOrders = readyToExecOrders,
                              readyToPickupOrders = readies,
                              busyKelners = orderKelnerId.keys.associateByTo(
                                  ConcurrentHashMap<Uid, Taca>(),
                                  { orderLabel -> orderKelnerId[orderLabel]!!.first },
                                  { orderLabel -> Taca(
                                      orderLabel,
                                      orderKelnerId[orderLabel]!!.second)}),
                              freeKelners = ConcurrentHashMap(),
                              executingOrders = orderKelnerId
                                  .mapValuesTo(
                                      ConcurrentHashMap(),
                                      { e -> e.value.first }),
                              nextToken = AtomicInteger(maxTokenId.value),
                              estimatorState = OrderExecEstimationState.create(
                                  festInfo.params.defaultOrderKeepMs,
                                  festInfo.params.defaultAvgMealMs),
                              queuesForMissingMeals = MapOfQueues(dish2Orders),
                              nextLabel = AtomicInteger(maxLabelId.getId() + 1)))
                    }
                  }
                }
              }
            }
          }
    }
  }
}
