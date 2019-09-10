package org.dan.jadalnia.app.order.pojo;

import org.dan.jadalnia.app.festival.menu.DishName

data class OrderItem(
        val name: DishName,
        val quantity: Int,
        val additions: MutableList<OrderItem>)

