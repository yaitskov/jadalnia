package org.dan.jadalnia.app.token

import org.dan.jadalnia.app.festival.pojo.Festival
import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.order.OpLog
import org.dan.jadalnia.app.user.Uid
import org.dan.jadalnia.app.ws.WsBroadcast
import org.dan.jadalnia.sys.error.JadEx.Companion.badRequest
import org.dan.jadalnia.util.collection.AsyncCache
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import javax.inject.Inject

class TokenService @Inject constructor(
    val tokenDao: TokenDao,
    val tokenBalanceCache: AsyncCache<Pair<Fid, Uid>, TokenBalance>,
    val wsBroadcast: WsBroadcast) {
  companion object {
    val log = LoggerFactory.getLogger(TokenService::class.java)
  }

  fun requestTokens(festival: Festival, customerUid: Uid, amount: TokenPoints)
      : CompletableFuture<TokenId> {
    if (amount.value < 1) {
      throw badRequest("min amount tokens to request is 1")
    }
    if (amount.value > 100) {
      throw badRequest("max amount tokens to request is 100")
    }
    return tokenBalanceCache.get(Pair(festival.fid(), customerUid))
        .thenCompose { balance ->
          val opLog = OpLog()
          balance.pending.updateAndGet { points -> TokenPoints(points.value + amount.value) }
          opLog.add {
            balance.pending.updateAndGet { points -> TokenPoints(points.value - amount.value) }
          }
          tokenDao.requestTokens(
              festival.fid(),
              TokenId(festival.nextToken.incrementAndGet()),
              amount, customerUid)
              .whenComplete { r, e ->
                if (e != null) {
                  opLog.rollback()
                }
              }
        }
  }

  fun approveTokens(
      festival: Festival,
      kasierUid: Uid,
      approveReq: TokenApproveReq): CompletableFuture<TokenId> {
    return tokenDao.findTokenForApprove(festival.fid(), approveReq)
        .thenCompose { tokenId ->
          tokenBalanceCache.get(Pair(festival.fid(), approveReq.customer))
              .thenCompose { balance ->
                val opLog = OpLog()
                balance.effective.updateAndGet { points -> TokenPoints(points.value + approveReq.amount.value) }
                opLog.add {
                  balance.effective.updateAndGet { points -> TokenPoints(points.value - approveReq.amount.value) }
                }
                tokenDao.approveTokens(festival.fid(), tokenId, kasierUid)
                    .thenApply {
                      wsBroadcast.notifyCustomers(
                          festival.fid(), listOf(approveReq.customer), TokenApprovedEvent(tokenId))
                      tokenId
                    }.whenComplete { r, e ->
                      if (e != null)  {
                        opLog.rollback()
                      }
                    }
              }
        }
  }
}