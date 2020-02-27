package org.dan.jadalnia.app.festival.pojo

import org.dan.jadalnia.app.festival.menu.DishName
import java.util.*
import java.util.Collections.singletonList
import java.util.concurrent.ConcurrentMap

class MapOfQueues (val map: ConcurrentMap<DishName, List<Taca>>) {

  fun isEmpty() = map.isEmpty()

  fun put(dish: DishName, taca: Taca)
      = map.compute(dish) { _, lst -> lst?.plus(taca) ?: singletonList(taca) }

  fun takeAll(dish: DishName) = map.remove(dish) ?: emptyList()

  fun keys() = TreeSet(map.keys)

  fun remove(meal: DishName, taca: Taca) {
    // implement
  }
}