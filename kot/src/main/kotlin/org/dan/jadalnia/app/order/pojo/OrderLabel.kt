package org.dan.jadalnia.app.order.pojo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonValue;


data class OrderLabel constructor (val name: String) {
    companion object {
        const val LETTERS = "ABCDEFGHIJKLMNOPQRSTUWXYZ"

        @JvmStatic
        @JsonCreator
        fun of(id: Int): OrderLabel {
            val letterIdx = id % LETTERS.length
            val n = 1 + id / LETTERS.length
            return OrderLabel("${LETTERS[letterIdx]}$n")
        }

        @JvmStatic
        @JsonCreator
        fun of(name: String) = OrderLabel(name)
    }

    fun toInt(): Int {
        val letterIndex = LETTERS.indexOf(name[0])
        val n = name.substring(1).toInt() - 1
        return n * LETTERS.length + letterIndex
    }

    @JsonIgnore
    fun getId(): Int = toInt()

    override fun toString() = name

    @JsonValue
    fun getJson(): String = name
}
