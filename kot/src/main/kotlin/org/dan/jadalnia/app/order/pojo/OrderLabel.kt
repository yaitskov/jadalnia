package org.dan.jadalnia.app.order.pojo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


data class OrderLabel(val id: Int, val name: String) {
    companion object {
        const val LETTERS = "ABCDEFGHIJKLMNOPQRSTUWXYZ"

        @JvmStatic
        fun of(id: Int): OrderLabel {
            val letterIdx = id % LETTERS.length
            val n = 1 + id / LETTERS.length
            return OrderLabel(id, "" + LETTERS[letterIdx] + n)
        }

        @JvmStatic
        @JsonCreator
        fun ofJson(name: String): OrderLabel {
            val letterIndex = LETTERS.indexOf(name[0])
            val n = name.substring(1).toInt() - 1
            return OrderLabel(n * LETTERS.length + letterIndex, name)
        }
    }

    @JsonValue
    override fun toString() = name
}
