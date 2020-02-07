package org.dan.jadalnia.app.order

import org.dan.jadalnia.app.festival.menu.DishName
import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.label.AsyncDao
import org.dan.jadalnia.app.order.pojo.OrderLabel
import org.dan.jadalnia.jooq.Tables.DELAYED_ORDER
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.CompletableFuture

class DelayedOrderDao : AsyncDao() {
  companion object {
    val log = LoggerFactory.getLogger(DelayedOrderDao::class.java)
  }

  fun load(fid: Fid): CompletableFuture<MutableMap<DishName, LinkedList<OrderLabel>>> {
    return execQuery { jooq ->
      KotlinSucks.group(jooq.select(
          DELAYED_ORDER.MISSING_DISH, DELAYED_ORDER.LABEL)
          .from(DELAYED_ORDER)
          .where(DELAYED_ORDER.FESTIVAL_ID.eq(fid))
          .stream()
          .map<Pair<DishName, OrderLabel>> { r -> Pair(
              r.get(DELAYED_ORDER.MISSING_DISH),
              r.get(DELAYED_ORDER.LABEL)) })
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
