package org.dan.jadalnia.app.order.line

import org.dan.jadalnia.app.festival.pojo.Festival
import org.dan.jadalnia.app.user.Uid
import java.time.Instant

class ActiveKelnerSearch  {
  fun find(before: Instant, festival: Festival): Set<Uid> {
    return festival.freeKelners
        .filterValues { info -> before.isBefore(info.freeSince) }
        .keys
  }
}