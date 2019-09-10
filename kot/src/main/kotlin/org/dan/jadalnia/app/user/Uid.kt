package org.dan.jadalnia.app.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.dan.jadalnia.sys.type.number.ImmutableNumber;

class Uid @JsonCreator constructor(id: Int): ImmutableNumber(id) {
    companion object {
        // jax-rsp
        @JvmStatic @JsonCreator
        fun valueOf(s: String) = Uid(Integer.valueOf(s))

        @JvmStatic
        fun of(id: Int) = Uid(id)
    }
}
