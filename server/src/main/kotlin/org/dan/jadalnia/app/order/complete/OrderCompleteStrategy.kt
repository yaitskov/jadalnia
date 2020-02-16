package org.dan.jadalnia.app.order.complete

import org.dan.jadalnia.app.festival.pojo.Festival
import org.dan.jadalnia.app.order.pojo.ProblemOrder
import org.dan.jadalnia.app.user.Uid
import java.util.concurrent.CompletableFuture

interface OrderCompleteStrategy {
  fun complete(festival: Festival, kelnerUid: Uid, problemOrder: ProblemOrder)
      : CompletableFuture<Uid>
}