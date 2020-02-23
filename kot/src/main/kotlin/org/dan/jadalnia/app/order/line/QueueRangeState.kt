package org.dan.jadalnia.app.order.line

import com.google.common.util.concurrent.AtomicLongMap
import org.dan.jadalnia.app.festival.menu.DishName

data class QueueRangeState(
    val orderMeals2Count: AtomicLongMap<Map<DishName, Int>>) {

  constructor(): this(AtomicLongMap.create())
}
