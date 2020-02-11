package org.dan.jadalnia.app.order.stats

import org.dan.jadalnia.app.festival.menu.DishName

data class MealsCount(val meals: Map<DishName, Int>)
