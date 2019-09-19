package org.dan.jadalnia.app.order.pojo;

import com.fasterxml.jackson.annotation.JsonCreator
import org.dan.jadalnia.app.festival.menu.DishName

data class OrderItem(
        val name: DishName,
        val quantity: Int,
        val additions: List<OrderItem>) {
    @JsonCreator constructor(
            name: DishName?,
            quantity: Int?,
            additions: List<OrderItem>?):
            this(
                    name ?: DishName("D"),
                    quantity ?: 1,
                    additions ?: emptyList<OrderItem>()) {
    }
}

