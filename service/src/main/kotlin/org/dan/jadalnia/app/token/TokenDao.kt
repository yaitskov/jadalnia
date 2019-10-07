package org.dan.jadalnia.app.token

import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.label.AsyncDao
import org.dan.jadalnia.app.user.Uid
import org.dan.jadalnia.jooq.Tables.TOKEN
import org.dan.jadalnia.sys.error.JadEx.Companion.conflict
import org.slf4j.LoggerFactory
import java.util.Optional.ofNullable
import java.util.concurrent.CompletableFuture

class TokenDao: AsyncDao() {
  companion object {
    val log = LoggerFactory.getLogger(TokenDao::class.java)
  }

  fun requestTokens(
      fid: Fid, tokenId: TokenId,
      amount: TokenPoints, customerId: Uid): CompletableFuture<TokenId> {
    return execQuery {
      jooq -> jooq.insertInto(TOKEN,
        TOKEN.TID,
        TOKEN.FESTIVAL_ID,
        TOKEN.CUSTOMER_ID,
        TOKEN.AMOUNT,
        TOKEN.OPERATION)
        .values(tokenId, fid, customerId, amount, TokenOp.Buy)
        .execute()
    }.thenApply {
      log.info("Customer {} requested token {}", customerId, tokenId)
      tokenId
    }
  }

  fun findTokenForApprove(fid: Fid, customer: Uid)
      : CompletableFuture<List<PreApproveTokenView>> {
    return execQuery { jooq -> jooq.select(TOKEN.TID, TOKEN.AMOUNT)
        .from(TOKEN)
        .where(TOKEN.FESTIVAL_ID.eq(fid),
            TOKEN.CUSTOMER_ID.eq(customer),
            TOKEN.KASIER_ID.isNull())
        .forUpdate()
        .fetch()
        .map { r -> PreApproveTokenView(r.get(TOKEN.TID), r.get(TOKEN.AMOUNT)) }
    }
  }

  fun approveToken(fid: Fid, tokenId: TokenId, kasierUid: Uid)
      : CompletableFuture<Void> {
    return execQuery { jooq -> jooq.update(TOKEN)
        .set(TOKEN.KASIER_ID, kasierUid)
        .where(
            TOKEN.FESTIVAL_ID.eq(fid),
            TOKEN.TID.eq(tokenId),
            TOKEN.KASIER_ID.isNull)
        .execute()
    }.thenAccept { rows ->
      if (rows != 1) {
        throw conflict("just token DB record was not updated: " + rows)
      } else {
        log.info("Token {} is approved by {}", tokenId, kasierUid)
      }
    }
  }

  fun maxTokenNumber(fid: Fid): CompletableFuture<TokenId> {
    return execQuery { jooq -> ofNullable(jooq
        .select(TOKEN.TID.max().`as`(TOKEN.TID))
        .from(TOKEN)
        .where(TOKEN.FESTIVAL_ID.eq(fid))
        .fetchOne())
        .map {r -> r.get(TOKEN.TID)}
        .orElseGet { TokenId(0) }
    }
  }

  fun loadNotApprovedBalance(fid: Fid, customer: Uid): CompletableFuture<TokenPoints> {
    return execQuery { jooq -> TokenPoints(jooq
        .select(TOKEN.AMOUNT, TOKEN.OPERATION)
        .from(TOKEN)
        .where(TOKEN.FESTIVAL_ID.eq(fid), TOKEN.CUSTOMER_ID.eq(customer))
        .fetch()
        .map { r -> r.get(TOKEN.AMOUNT).value * r.get(TOKEN.OPERATION).sign() }
        .stream()
        .mapToInt{ a -> a }
        .sum())
    }
  }

  fun loadBalance(fid: Fid, customer: Uid): CompletableFuture<TokenPoints> {
    return execQuery { jooq -> TokenPoints(jooq
        .select(TOKEN.AMOUNT, TOKEN.OPERATION)
        .from(TOKEN)
        .where(TOKEN.FESTIVAL_ID.eq(fid),
            TOKEN.CUSTOMER_ID.eq(customer),
            TOKEN.KASIER_ID.isNotNull)
        .fetch()
        .map { r -> r.get(TOKEN.AMOUNT).value * r.get(TOKEN.OPERATION).sign() }
        .stream()
        .mapToInt { a -> a }
        .sum())
    }
  }

  fun getCustomerPendingTokensByIds(customerId: Uid, tokens: List<TokenId>)
      : CompletableFuture<List<PreApproveTokenView>> {
    return execQuery { jooq -> jooq
        .select(TOKEN.TID, TOKEN.AMOUNT)
        .from(TOKEN)
        .where(TOKEN.CUSTOMER_ID.eq(customerId),
            TOKEN.TID.`in`(tokens),
            TOKEN.KASIER_ID.isNull)
        .fetch()
        .map { r -> PreApproveTokenView(
            r.get(TOKEN.TID), r.get(TOKEN.AMOUNT))
        }
    }
  }
}