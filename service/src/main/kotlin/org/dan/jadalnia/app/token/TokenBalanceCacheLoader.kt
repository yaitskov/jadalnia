package org.dan.jadalnia.app.token

import com.google.common.cache.CacheLoader
import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.order.OrderDao
import org.dan.jadalnia.app.user.Uid
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

class TokenBalanceCacheLoader @Inject constructor(
    val tokenDao: TokenDao,
    val orderDao: OrderDao)
  : CacheLoader<Pair<Fid, Uid>, CompletableFuture<TokenBalance>>() {

  override fun load(key: Pair<Fid, Uid>): CompletableFuture<TokenBalance> {
    return tokenDao.loadBalance(key.first, key.second)
        .thenCompose { approvedPoints ->
          orderDao.loadSumOfPaidOrders(key.first, key.second).thenCompose { spended ->
            tokenDao.loadNotApprovedBalance(key.first, key.second).thenApply {
              notApprovedPoints ->
              TokenBalance(
                  effective = AtomicReference(approvedPoints.minus(spended)),
                  pending = AtomicReference(notApprovedPoints.minus(spended)))
            }
          }
        }
  }
}