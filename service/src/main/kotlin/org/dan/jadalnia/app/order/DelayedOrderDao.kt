package org.dan.jadalnia.app.order

import org.dan.jadalnia.app.festival.menu.DishName
import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.festival.pojo.Taca
import org.dan.jadalnia.app.label.AsyncDao
import org.dan.jadalnia.app.order.pojo.OrderLabel
import org.dan.jadalnia.jooq.Tables.DELAYED_ORDER
import org.dan.jadalnia.jooq.Tables.ORDERS
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

class DelayedOrderDao : AsyncDao() {
  companion object {
    val log = LoggerFactory.getLogger(DelayedOrderDao::class.java)
  }

  fun load(fid: Fid): CompletableFuture<ConcurrentMap<DishName, List<Taca>>> {
    return execQuery { jooq -> ConcurrentHashMap(jooq.select(
        DELAYED_ORDER.MISSING_DISH, DELAYED_ORDER.LABEL, ORDERS.PAID_AT)
        .from(DELAYED_ORDER)
        .innerJoin(ORDERS)
        .on(ORDERS.FESTIVAL_ID.eq(DELAYED_ORDER.FESTIVAL_ID)
            .and(ORDERS.LABEL.eq(DELAYED_ORDER.LABEL)))
        .where(DELAYED_ORDER.FESTIVAL_ID.eq(fid))
        .fetch()
        .groupBy(
            { r -> r.get(DELAYED_ORDER.MISSING_DISH) },
            { r -> Taca(r.get(DELAYED_ORDER.LABEL), r.get(ORDERS.PAID_AT)) }))
    }
  }

  fun remove(fid: Fid, order: OrderLabel): CompletableFuture<Void> {
    return execQuery { jooq -> jooq
        .delete(DELAYED_ORDER)
        .where()
        .execute()
    }.thenAccept {
      log.info("order {}:{} is marked as not delayed", fid, order)
    }
  }

  fun delayed(fid: Fid, troubleDish: DishName, order: OrderLabel):
      CompletableFuture<Void> {
    return execQuery { jooq ->
      jooq.insertInto(DELAYED_ORDER,
          DELAYED_ORDER.FESTIVAL_ID,
          DELAYED_ORDER.LABEL,
          DELAYED_ORDER.MISSING_DISH)
          .values(fid, order, troubleDish)
          .execute()
    }.thenAccept {
      log.info("stored delayed order {} in fid {}", order, fid)
    }
  }
}
