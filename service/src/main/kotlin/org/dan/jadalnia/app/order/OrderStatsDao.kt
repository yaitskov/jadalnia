package org.dan.jadalnia.app.order

import org.dan.jadalnia.app.festival.menu.DishName
import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.label.AsyncDao
import org.dan.jadalnia.app.order.pojo.OrderState
import org.dan.jadalnia.app.order.stats.MealsCount
import org.dan.jadalnia.jooq.Tables.ORDERS
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture

class OrderStatsDao : AsyncDao() {
  companion object {
    val log = LoggerFactory.getLogger(OrderDao::class.java)
  }

  fun servedMeals(fid: Fid): CompletableFuture<MealsCount> {
    return execQuery { jooq ->
      val result = HashMap<DishName, Int>()
      jooq.select(ORDERS.REQUIREMENTS)
          .from(ORDERS)
          .where(ORDERS.FESTIVAL_ID.eq(fid),
              ORDERS.STATE.`in`(OrderState.Ready, OrderState.Handed))
          .stream()
          .flatMap { r -> r.get(ORDERS.REQUIREMENTS).stream() }
          .forEach { item ->
            result.merge(item.name, item.quantity) { a, b -> a + b }
          }
       MealsCount(result)
    }
  }
}