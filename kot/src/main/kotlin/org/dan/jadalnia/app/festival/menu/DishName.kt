package org.dan.jadalnia.app.festival.menu;

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

data class DishName @JsonCreator constructor(val name: String) {
    @JsonValue
    override fun toString(): String = super.toString()
}

