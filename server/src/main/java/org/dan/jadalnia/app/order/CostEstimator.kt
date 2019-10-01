package org.dan.jadalnia.app.order

import org.dan.jadalnia.app.festival.menu.DishName
import org.dan.jadalnia.app.festival.menu.MenuItem
import org.dan.jadalnia.app.festival.pojo.Festival
import org.dan.jadalnia.app.order.pojo.OrderItem
import org.dan.jadalnia.app.token.TokenPoints
import org.dan.jadalnia.sys.error.JadEx.Companion.badRequest

class CostEstimator {
  fun howMuchFor(festival: Festival, items: List<OrderItem>): TokenPoints {
    val prices = priceTable(festival.info.get().menu)
    return items.stream().map { item -> howMuchFor(prices, item) }
        .reduce(TokenPoints(0)) {
          a, b -> TokenPoints(a.value + b.value)
        }
  }

  fun howMuchFor(prices: Map<DishName, TokenPoints>, item: OrderItem): TokenPoints {
    return prices.getOrElse(
        item.name,
        {
          throw badRequest("dish not available", "dish", item.name)
        })
  }

  private fun priceTable(menu: List<MenuItem>): Map<DishName, TokenPoints> {
    val result = HashMap<DishName, TokenPoints>()
    for (item in menu) {
      if (item.disabled) {
        continue
      }
      result[item.name] = TokenPoints(item.price.toInt())
    }
    return result
  }
}