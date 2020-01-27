package org.dan.jadalnia.app.festival.ctx

import com.google.common.cache.CacheLoader
import org.dan.jadalnia.app.festival.FestivalDao
import org.dan.jadalnia.app.festival.pojo.Festival
import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.festival.pojo.MapOfQueues
import org.dan.jadalnia.app.label.LabelDao
import org.dan.jadalnia.app.order.OrderAggregator
import org.dan.jadalnia.app.order.OrderDao
import org.dan.jadalnia.app.order.pojo.OrderLabel
import org.dan.jadalnia.app.token.TokenDao
import org.dan.jadalnia.app.user.Uid
import org.dan.jadalnia.app.ws.WsBroadcast
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.completedFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock
import java.util.stream.Collectors.toConcurrentMap
import javax.inject.Inject

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
              orderDao.loadReadyToExecOrders(fid).thenCompose { readyToExecOrders ->
                orderDao.loadReadyOrders(fid).thenCompose { readies ->
                  orderDao.loadExecutingOrders(fid).thenCompose { orderKelnerId ->
                    completedFuture(
                        Festival(
                            info = AtomicReference(festInfo),
                            readyToExecOrders = LinkedBlockingDeque(readyToExecOrders),
                            readyToPickupOrders = ConcurrentHashMap(readies),
                            busyKelners = orderKelnerId.entries.stream()
                                .collect(toConcurrentMap<
                                    Map.Entry<OrderLabel, Uid>,
                                    Uid, OrderLabel>(
                                    { e -> e.value },
                                    { e -> e.key })),
                            freeKelners = ConcurrentHashMap(),
                            executingOrders = ConcurrentHashMap(orderKelnerId),
                            nextToken = AtomicInteger(maxTokenId.value),
                            queuesForMissingMeals = MapOfQueues(
                                ReentrantLock(false),
                                HashMap()),
                            nextLabel = AtomicInteger(maxLabelId.getId() + 1)))
                  }
                }
              }
            }
          }
    }
  }
}
