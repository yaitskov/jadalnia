package org.dan.jadalnia.app.order.pref

import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.order.KelnerPerformacenDao
import org.dan.jadalnia.app.token.TokenPoints
import org.dan.jadalnia.app.user.UserDao
import org.dan.jadalnia.app.user.UserType
import java.util.concurrent.CompletableFuture
import javax.inject.Inject

class KelnerPerfomanceService @Inject constructor(
    val userDao: UserDao,
    val dao: KelnerPerformacenDao) {

  fun preformance(fid: Fid)
      : CompletableFuture<List<KelnerPerformanceRow>> {
    return dao.performance(fid).thenCompose { performancebyUid ->
      userDao.listByType(fid, UserType.Kelner).thenApply { users ->
        users.map { link ->
          val kelnerPerformance = performancebyUid[link.uid]
          if (kelnerPerformance == null) {
            KelnerPerformanceRow(link.name, 0, TokenPoints(0))
          } else {
            KelnerPerformanceRow(link.name, kelnerPerformance.first,
                kelnerPerformance.second)
          }
        }
      }
    }
  }
}