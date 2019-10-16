package org.dan.jadalnia.app.token;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.dan.jadalnia.sys.type.number.ImmutableNumber;

class TokenId @JsonCreator constructor (id: Int): ImmutableNumber(id) {
    companion object {
        // jax-rsp
        @JsonCreator
        fun valueOf(s: String): TokenId = TokenId(Integer.valueOf(s))

        fun of(id: Int): TokenId = TokenId(id)
    }
}
