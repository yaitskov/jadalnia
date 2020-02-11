package org.dan.jadalnia.app.token

import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.label.AsyncDao
import org.dan.jadalnia.jooq.Tables.TOKEN
import org.jooq.Condition
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.util.Optional.ofNullable
import java.util.concurrent.CompletableFuture

class TokenStatsDao : AsyncDao() {
  companion object {
    val log = LoggerFactory.getLogger(TokenStatsDao::class.java)
  }

  fun tokenStats(fid: Fid): CompletableFuture<TokenStats> {
    return sumApprovedTokens(fid, TokenOp.Buy)
        .thenCompose { bought ->
          sumApprovedTokens(fid, TokenOp.Sell)
              .thenCompose { returns ->
                sumNotApprovedTokens(fid, TokenOp.Buy)
                    .thenCompose { pendingBought ->
                      sumNotApprovedTokens(fid, TokenOp.Sell)
                          .thenCompose { pendingReturns ->
                            CompletableFuture.completedFuture(
                                TokenStats(bought, returns,
                                    pendingBought, pendingReturns)
                            )
                          }
                    }
              }
        }
  }

  fun sumApprovedTokens(fid: Fid, tokenOp: TokenOp)
      = sumTokens(fid, tokenOp, TOKEN.KASIER_ID.isNotNull)

  fun sumTokens(fid: Fid, tokenOp: TokenOp, condition: Condition)
      : CompletableFuture<TokenPoints> {
    return execQuery { jooq ->
      ofNullable(jooq.select(TOKEN.AMOUNT.sum().`as`("s"))
          .from(TOKEN)
          .where(TOKEN.FESTIVAL_ID.eq(fid),
              TOKEN.OPERATION.eq(tokenOp),
              condition)
          .fetchOne("s", BigDecimal::class.java))
          .map(BigDecimal::toInt)
          .map(::TokenPoints)
          .orElseGet { TokenPoints(0) }
    }
  }

  fun sumNotApprovedTokens(fid: Fid, tokenOp: TokenOp)
      = sumTokens(fid, tokenOp, TOKEN.KASIER_ID.isNull)
}