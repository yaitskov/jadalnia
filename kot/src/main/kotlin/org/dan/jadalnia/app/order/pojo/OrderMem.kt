package org.dan.jadalnia.app.order.pojo

import org.dan.jadalnia.app.festival.menu.DishName
import org.dan.jadalnia.app.token.TokenPoints
import org.dan.jadalnia.app.user.Uid
import org.dan.jadalnia.util.collection.MapQ.QueueInsertIdx
import java.util.concurrent.atomic.AtomicReference

data class OrderMem(
    val customer: Uid,
    val state: AtomicReference<OrderState>,
    val label: OrderLabel,
    val insertQueueIdx: AtomicReference<QueueInsertIdx>,
    val cost: AtomicReference<TokenPoints>,
    val items: AtomicReference<List<OrderItem>>) {

  fun toContentMap(): Map<DishName, Int>
      = items.get().associate { item -> Pair(item.name, item.quantity) }
}
