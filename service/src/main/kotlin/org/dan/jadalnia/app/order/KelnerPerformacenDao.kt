package org.dan.jadalnia.app.order

import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.label.AsyncDao
import org.dan.jadalnia.app.order.pojo.OrderState
import org.dan.jadalnia.app.token.TokenPoints
import org.dan.jadalnia.app.user.Uid
import org.dan.jadalnia.jooq.Tables.ORDERS
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.util.concurrent.CompletableFuture

class KelnerPerformacenDao : AsyncDao() {
  companion object {
    val log = LoggerFactory.getLogger(OrderDao::class.java)
  }

  fun performance(fid: Fid): CompletableFuture<Map<Uid, Pair<Int, TokenPoints>>> {
    return execQuery { jooq ->
      val result = HashMap<Uid, Pair<Int, TokenPoints>>()
      jooq.select(
          ORDERS.KELNER_ID,
          ORDERS.POINTS_COST.sum().`as`("costSum"),
          ORDERS.OID.count().`as`("orderCount"))
          .from(ORDERS)
          .where(ORDERS.FESTIVAL_ID.eq(fid),
              ORDERS.STATE.`in`(OrderState.Ready, OrderState.Handed),
              ORDERS.KELNER_ID.isNotNull)
          .groupBy(ORDERS.KELNER_ID)
          .fetch()
          .forEach { r -> result[r.get(ORDERS.KELNER_ID)] = Pair(
              r.get("orderCount", BigDecimal::class.java).toInt(),
              TokenPoints(r.get("costSum", BigDecimal::class.java).toInt()))
          }
      result
    }
  }
}