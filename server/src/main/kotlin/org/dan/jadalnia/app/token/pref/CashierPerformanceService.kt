package org.dan.jadalnia.app.order.pref

import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.token.CashierPerformacenDao
import org.dan.jadalnia.app.token.TokenOp
import org.dan.jadalnia.app.token.TokenPoints
import org.dan.jadalnia.app.user.UserDao
import org.dan.jadalnia.app.user.UserType
import java.util.concurrent.CompletableFuture
import javax.inject.Inject

class CashierPerformanceService @Inject constructor(
    val userDao: UserDao,
    val dao: CashierPerformacenDao) {

  fun performance(fid: Fid)
      : CompletableFuture<List<CashierPerformanceRow>> {
    return dao.performance(fid, TokenOp.Buy).thenCompose { performanceByUid ->
      userDao.listByType(fid, UserType.Kasier).thenApply { users ->
        users.map { link ->
          val kasierPerformance = performanceByUid[link.uid]
          if (kasierPerformance == null) {
            CashierPerformanceRow(link.name, 0, TokenPoints(0))
          } else {
            CashierPerformanceRow(link.name, kasierPerformance.first,
                kasierPerformance.second)
          }
        }
      }
    }
  }
}