package org.dan.jadalnia.app.token

import org.dan.jadalnia.app.festival.pojo.Festival
import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.order.OpLog
import org.dan.jadalnia.app.user.Uid
import org.dan.jadalnia.app.ws.WsBroadcast
import org.dan.jadalnia.sys.error.JadEx
import org.dan.jadalnia.sys.error.JadEx.Companion.badRequest
import org.dan.jadalnia.sys.error.JadEx.Companion.conflict
import org.dan.jadalnia.sys.error.JadEx.Companion.internalError
import org.dan.jadalnia.util.Futures
import org.dan.jadalnia.util.collection.AsyncCache
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.completedFuture
import javax.inject.Inject


class TokenService @Inject constructor(
    val tokenDao: TokenDao,
    val tokenBalanceCache: AsyncCache<Pair<Fid, Uid>, TokenBalance>,
    val wsBroadcast: WsBroadcast) {
  companion object {
    val log = LoggerFactory.getLogger(TokenService::class.java)
  }

  fun requestTokensPurchase(festival: Festival, customerUid: Uid,
                            amount: TokenPoints, tokOp: TokenOp)
      : CompletableFuture<TokenId> {
    if (amount.value < 1) {
      throw badRequest("min amount tokens to request is 1")
    }
    if (amount.value > 100) {
      throw badRequest("max amount tokens to request is 100")
    }
    return requestTokensNoValidation(festival, customerUid, amount, tokOp)
  }

  fun requestTokensOnce(balance: TokenBalance, festival: Festival,
                        customerUid: Uid, amount: TokenPoints, tokOp: TokenOp)
      : CompletableFuture<TokenId> {
    val opLog = OpLog()
    val currentBalance = balance.pending.get()
    val change = amount.value * tokOp.sign()
    val newBalance = currentBalance.value + change
    if (newBalance < 0) {
      throw badRequest("Balance would be less than 0")
    }
    val fid = festival.fid()
    var attempts = 10;
    while (--attempts > 0) {
      if (balance.pending.compareAndSet(currentBalance, TokenPoints(newBalance))) {
        opLog.add {
          balance.pending.updateAndGet { points ->
            TokenPoints(points.value - change)
          }
        }
        return tokenDao.requestTokens(
            fid,
            TokenId(festival.nextToken.incrementAndGet()),
            amount, customerUid, tokOp)
            .whenComplete { _, e ->
              if (e != null) {
                opLog.rollback()
              }
            }
      } else {
        log.info("Collision on balance update {} for {}:{}",
            newBalance, fid, customerUid)
      }
    }
    throw internalError("To many attempts to update balance")
  }

  fun requestTokensNoValidation(
      festival: Festival, customerUid: Uid,
      amount: TokenPoints, tokOp: TokenOp): CompletableFuture<TokenId> {
    return tokenBalanceCache.get(Pair(festival.fid(), customerUid))
        .thenCompose { balance ->
          requestTokensOnce(balance, festival, customerUid, amount, tokOp)
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
                      val currentBalance = balance.effective.get()
                      val nextBalance = currentBalance.plus(token.amount)
                      if (nextBalance.value >= 0) {
                        if (balance.effective.compareAndSet(currentBalance, nextBalance)) {
                          log.info("Increase effective balance {} by {} for {}:{}",
                              nextBalance, token.amount, fid, approveReq.customer)
                          tokenDao.approveToken(fid, token.tokenId, kasierUid).thenApply {
                            Optional.of(token)
                          }.exceptionally { e ->
                            log.error("Attempt to approve token {} failed", token.tokenId, e)
                            balance.effective.updateAndGet { tokens -> tokens.minus(token.amount) }
                            Optional.empty()
                          }
                        } else {
                          log.error("Reject token request {}:{} due effective balance changed",
                              fid, token.tokenId)
                          completedFuture(Optional.empty())
                        }
                      } else {
                        log.error("Reject token request {}:{} due effective balance would be negative",
                            fid, token.tokenId)
                        completedFuture(Optional.empty())
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