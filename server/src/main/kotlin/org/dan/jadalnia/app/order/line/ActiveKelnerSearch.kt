package org.dan.jadalnia.app.order.line

import org.dan.jadalnia.app.festival.pojo.Festival
import org.dan.jadalnia.app.user.Uid
import java.time.Instant
import java.util.stream.Collectors.toSet
import java.util.stream.Stream

class ActiveKelnerSearch  {
  fun find(before: Instant, festival: Festival): Set<Uid> {
    return Stream.concat(
        festival.busyKelners.keys.stream(),
        festival.freeKelners
            .filterValues { info -> before.isBefore(info.freeSince) }
            .keys.stream())
        .collect(toSet())
  }
}