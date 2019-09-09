package org.dan.jadalnia.sys.type.number;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

open class ImmutableNumber
@JsonCreator
constructor (val value: Int)
    : AbstractNum() {
    @Override
    @JsonValue
    override fun intValue(): Int = value

    @JsonIgnore
    open fun getValidateValue(): Int = value

    override fun hashCode(): Int = value
    override fun equals(other: Any?): Boolean {
        if (other is AbstractNum) {
            return value == other.intValue()
        }
        return false
    }
}
