package org.dan.jadalnia.app.order.pojo;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.dan.jadalnia.sys.type.number.ImmutableNumber;

class Oid @JsonCreator constructor (id: Int): ImmutableNumber(id) {
    companion object {
        // jax-rsp
        @JsonCreator
        fun valueOf(s: String): Oid = Oid(Integer.valueOf(s))

        fun of(id: Int): Oid = Oid(id)
    }
}
