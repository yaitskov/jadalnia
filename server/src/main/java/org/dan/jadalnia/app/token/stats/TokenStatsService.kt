package org.dan.jadalnia.app.order.stats

import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.token.TokenStatsDao
import javax.inject.Inject

class TokenStatsService @Inject constructor(val tokenStatsDao: TokenStatsDao) {
  fun tokenStats(fid: Fid) = tokenStatsDao.tokenStats(fid)
}