package org.dan.jadalnia.app.festival.pojo

import org.dan.jadalnia.app.festival.menu.DishName
import org.dan.jadalnia.app.order.pojo.OrderLabel
import java.util.*
import java.util.Collections.singletonList
import java.util.concurrent.locks.ReentrantLock

class MapOfQueues (
    val lock: ReentrantLock,
    val map: MutableMap<DishName, LinkedList<OrderLabel>>) {

  fun isEmpty(): Boolean {
    lock.lockInterruptibly()
    try {
      return map.isEmpty()
    } finally {
      lock.unlock()
    }
  }

  fun put(dish: DishName, label: OrderLabel) {
    lock.lockInterruptibly()
    try {
      if (!map.containsKey(dish)) {
        map[dish] = LinkedList(singletonList(label))
      } else {
        map[dish]!!.add(label)
      }
    } finally {
      lock.unlock()
    }
  }

  fun takeAll(dish: DishName): LinkedList<OrderLabel> {
    lock.lockInterruptibly()
    try {
      return map.remove(dish) ?: LinkedList()
    } finally {
      lock.unlock()
    }
  }

  fun remove(dish: DishName, order: OrderLabel): OrderLabel? {
    lock.lockInterruptibly()
    try {
      val lst = map.get(dish)
      if (lst != null && !lst.isEmpty()) {
        if (lst.first == order) {
          return lst.removeFirst()
        }
      }
      return null;
    } finally {
      lock.unlock()
    }
  }

  fun keys(): Set<DishName> {
    lock.lockInterruptibly()
    try {
      return TreeSet(map.keys)
    } finally {
      lock.unlock()
    }
  }
}