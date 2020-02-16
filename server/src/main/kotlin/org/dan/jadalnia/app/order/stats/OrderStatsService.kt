package org.dan.jadalnia.app.order.stats

import org.dan.jadalnia.app.festival.menu.DishName
import org.dan.jadalnia.app.festival.pojo.Festival
import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.order.OrderStatsDao
import org.dan.jadalnia.app.order.pojo.OrderLabel
import org.dan.jadalnia.app.order.pojo.OrderMem
import org.dan.jadalnia.app.order.pojo.OrderState
import org.dan.jadalnia.util.Futures
import org.dan.jadalnia.util.collection.AsyncCache
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.completedFuture
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

class OrderStatsService @Inject constructor(
    val orderStatsDao: OrderStatsDao,
    val orderCacheByLabel: AsyncCache<Pair<Fid, OrderLabel>, OrderMem>) {

  fun paidDemand(festival: Festival): CompletableFuture<MealsCount> {
    val fid = festival.fid()
    return Futures.reduce(
        { label, map ->
          orderCacheByLabel.get(Pair(fid, label)).thenCompose { order ->
            if (order.state.get() == OrderState.Paid) {
              order.items.get().forEach { item ->
                map.merge(item.name, item.quantity) { a, b -> a + b }
              }
            }
            completedFuture(map)
          }
        },
        ConcurrentHashMap<DishName, Int>(),
        festival.readyToExecOrders.iterator())
        .thenApply(::MealsCount)
  }

  fun servedMeals(festival: Festival): CompletableFuture<MealsCount> {
    return orderStatsDao.servedMeals(festival.fid());
  }
}