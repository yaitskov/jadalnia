package org.dan.jadalnia.app.token

import com.fasterxml.jackson.annotation.JsonCreator
import org.dan.jadalnia.sys.type.number.ImmutableNumber

class TokenPoints constructor(value: Int): ImmutableNumber(value) {
  companion object {
    // jax-rsp
    @JvmStatic @JsonCreator
    fun valueOf(s: String) = TokenPoints(Integer.valueOf(s))

    @JvmStatic @JsonCreator
    fun valueOf(id: Int) = TokenPoints(id)
  }

  fun plus(other: TokenPoints) = TokenPoints(value + other.value)

  fun minus(other: TokenPoints) = TokenPoints(value - other.value)

  fun scale(k: Int) = TokenPoints(value * k)
}