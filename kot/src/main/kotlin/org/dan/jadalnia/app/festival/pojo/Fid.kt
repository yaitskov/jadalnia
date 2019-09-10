package org.dan.jadalnia.app.festival.pojo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.dan.jadalnia.sys.type.number.ImmutableNumber;

import javax.validation.constraints.Min;

class Fid(v: Int): ImmutableNumber(v) {
    companion object {
        const val FESTIVAL_ID_SHOULD_BE_A_POSITIVE_NUMBER
                = "festival id should be a positive number"

        @JvmStatic
        @JsonCreator // jax-rsp
        fun valueOf(s: String): Fid = Fid(Integer.valueOf(s))
        @JvmStatic
        @JsonCreator
        fun of(id: Int): Fid = Fid(id)
    }

    @JsonIgnore
    @Min(value = 1, message = FESTIVAL_ID_SHOULD_BE_A_POSITIVE_NUMBER)
    override fun getValidateValue(): Int = super.getValidateValue();
}
