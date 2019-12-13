package org.dan.jadalnia.app.token

import org.dan.jadalnia.app.festival.pojo.Festival
import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.order.OpLog
import org.dan.jadalnia.app.user.Uid
import org.dan.jadalnia.app.ws.WsBroadcast
import org.dan.jadalnia.sys.error.JadEx
import org.dan.jadalnia.sys.error.JadEx.Companion.badRequest
import org.dan.jadalnia.sys.error.JadEx.Companion.conflict
import org.dan.jadalnia.util.Futures
import org.dan.jadalnia.util.collection.AsyncCache
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.CompletableFuture
import javax.inject.Inject


typealias RejectReason = String

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
              .whenComplete { _, e ->
                if (e != null) {
                  opLog.rollback()
                }
              }
        }
  }

  fun findTokensForApprove(festival: Festival, customer: Uid)
      : CompletableFuture<List<PreApproveTokenView>> {
    return tokenDao.findTokenForApprove(festival.fid(), customer)
  }

  private fun validate(dbTokens: List<PreApproveTokenView>,
                       approveReq: TokensApproveReq) {
    if (dbTokens.size != approveReq.tokens.size) {
      throw conflict("pending token set changed, retry")
    }
  }


  fun approveTokens(
      festival: Festival,
      kasierUid: Uid,
      approveReq: TokensApproveReq): CompletableFuture<List<PreApproveTokenView>> {
    val fid = festival.fid()
    return tokenDao.getCustomerPendingTokensByIds(
        approveReq.customer, approveReq.tokens)
        .thenCompose { tokens ->
          validate(tokens, approveReq)
          tokenBalanceCache.get(Pair(fid, approveReq.customer))
              .thenCompose { balance ->
                Futures.allOf(
                    tokens.map { token ->
                      log.info("Kasier {} tries to approve token {}", kasierUid, token.tokenId)
                      tokenDao.approveToken(fid, token.tokenId, kasierUid).thenAccept {
                        balance.effective.updateAndGet {
                          points ->
                          log.info("Increase effective balance by {} for {}",
                              token.amount, approveReq.customer)
                          points.plus(token.amount)
                        }
                      }.thenApply { Optional.of(token)
                      }.exceptionally { e ->
                        log.error("Attempt to approve token {} failed", token.tokenId, e)
                        Optional.empty()
                      }
                    })
                    .thenApply { tokenUpdates ->
                      val successUpdates = tokenUpdates
                          .filter { update -> update.isPresent }
                          .map { update -> update.get() }
                      successUpdates.forEach { successUpdate ->
                        wsBroadcast.notifyCustomers(
                            fid, listOf(approveReq.customer),
                            TokenApprovedEvent(successUpdate.tokenId))
                      }
                      successUpdates
                    }
              }
        }
  }

  fun getBalance(festival: Festival, uid: Uid): CompletableFuture<TokenBalanceView> {
    return tokenBalanceCache.get(Pair(festival.fid(), uid))
        .thenApply { balance ->
          TokenBalanceView(
              pendingTokens = balance.pending.get(),
              effectiveTokens = balance.effective.get())
        }
  }

  fun showVisitorTokenRequest(fid: Fid, tokenReqId: TokenId): CompletableFuture<TokenRequestVisitorView> {
    return tokenDao.getToken(fid, tokenReqId).thenApply {
      viewO -> viewO.orElseThrow { JadEx.notFound("token request not found")}
    }
  }
}