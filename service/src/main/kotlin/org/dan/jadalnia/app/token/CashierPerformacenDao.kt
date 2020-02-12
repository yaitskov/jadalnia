package org.dan.jadalnia.app.token

import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.label.AsyncDao
import org.dan.jadalnia.app.user.Uid
import org.dan.jadalnia.jooq.Tables.TOKEN
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.util.concurrent.CompletableFuture

class CashierPerformacenDao : AsyncDao() {
  companion object {
    val log = LoggerFactory.getLogger(CashierPerformacenDao::class.java)
  }

  fun performance(fid: Fid, tokOp: TokenOp)
      : CompletableFuture<Map<Uid, Pair<Int, TokenPoints>>> {
    return execQuery { jooq ->
      val result = HashMap<Uid, Pair<Int, TokenPoints>>()
      jooq.select(
          TOKEN.KASIER_ID,
          TOKEN.AMOUNT.sum().`as`("costSum"),
          TOKEN.TID.count().`as`("requestCount"))
          .from(TOKEN)
          .where(TOKEN.FESTIVAL_ID.eq(fid),
              TOKEN.OPERATION.eq(tokOp),
              TOKEN.KASIER_ID.isNotNull)
          .groupBy(TOKEN.KASIER_ID)
          .fetch()
          .forEach { r -> result[r.get(TOKEN.KASIER_ID)] = Pair(
              r.get("requestCount", BigDecimal::class.java).toInt(),
              TokenPoints(r.get("costSum", BigDecimal::class.java).toInt()))
          }
      result
    }
  }
}