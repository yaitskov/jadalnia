package org.dan.jadalnia.app.token

import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.label.AsyncDao
import org.dan.jadalnia.app.user.Uid
import org.dan.jadalnia.jooq.Tables.TOKEN
import org.dan.jadalnia.sys.error.JadEx.Companion.badRequest
import org.dan.jadalnia.sys.error.JadEx.Companion.internalError
import java.util.*
import java.util.Optional.ofNullable
import java.util.concurrent.CompletableFuture

class TokenDao: AsyncDao() {
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
    }.thenApply { tokenId }
  }

  fun findTokenForApprove(fid: Fid, approveReq: TokenApproveReq)
      : CompletableFuture<TokenId> {
    return execQuery { jooq -> Optional.of(jooq.select(TOKEN.TID)
        .from(TOKEN)
        .where(TOKEN.FESTIVAL_ID.eq(fid),
            TOKEN.CUSTOMER_ID.eq(approveReq.customer),
            TOKEN.KASIER_ID.isNull(),
            TOKEN.AMOUNT.eq(approveReq.amount))
        .fetchOne())
        .map { r -> r.get(TOKEN.TID) }
        .orElseThrow { badRequest("no token for approve") }
    }
  }

  fun approveTokens(fid: Fid, tokenId: TokenId,
                    kasierUid: Uid): CompletableFuture<TokenId> {
    return execQuery { jooq -> jooq.update(TOKEN)
        .set(TOKEN.KASIER_ID, kasierUid)
        .where(
            TOKEN.TID.eq(tokenId),
            TOKEN.FESTIVAL_ID.eq(fid),
            TOKEN.KASIER_ID.isNull)
        .execute()
    }
        .thenAccept { rows ->
          if (rows != 1) {
            throw internalError("token DB record was not updated")
          }
        }.thenApply { tokenId }
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
    return execQuery { jooq -> ofNullable(jooq
        .select(TOKEN.AMOUNT.sum().`as`(TOKEN.AMOUNT))
        .from(TOKEN)
        .where(TOKEN.FESTIVAL_ID.eq(fid), TOKEN.CUSTOMER_ID.eq(customer))
        .fetchOne())
        .map { r -> r.get(TOKEN.AMOUNT) }
        .orElseGet { TokenPoints(0) }
    }
  }

  fun loadBalance(fid: Fid, customer: Uid): CompletableFuture<TokenPoints> {
    return execQuery { jooq -> ofNullable(jooq
        .select(TOKEN.AMOUNT.sum().`as`(TOKEN.AMOUNT))
        .from(TOKEN)
        .where(TOKEN.FESTIVAL_ID.eq(fid),
            TOKEN.CUSTOMER_ID.eq(customer),
            TOKEN.KASIER_ID.isNotNull())
        .fetchOne())
        .map { r -> r.get(TOKEN.AMOUNT) }
        .orElseGet { TokenPoints(0) }
    }
  }
}