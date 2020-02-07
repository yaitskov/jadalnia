package org.dan.jadalnia.app.festival.menu;

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

data class DishName constructor(val name: String): Comparable<DishName> {
    companion object {
        @JvmStatic
        @JsonCreator
        fun of(s: String) = DishName(s)
    }

    @JsonValue
    override fun toString(): String = name

    override fun compareTo(other: DishName) = name.compareTo(other.name)
}

