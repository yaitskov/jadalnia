package org.dan.jadalnia.app.order.pojo

import org.dan.jadalnia.app.festival.menu.DishName

class ProblemOrder(val label: OrderLabel, val meal: DishName?) {
  constructor(label: OrderLabel) : this(label, null)
}
