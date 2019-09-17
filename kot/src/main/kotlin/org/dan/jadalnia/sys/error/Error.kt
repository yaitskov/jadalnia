package org.dan.jadalnia.sys.error;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.UUID;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes(
        value = [
            JsonSubTypes.Type(
                    value = TemplateError::class,
                    name = "tp")
        ]
)
open class Error(val id: String, val message: String) {
    companion object {
        fun genId() = UUID.randomUUID().toString()
    }

    constructor(message: String): this(genId(), message)

    override fun toString(): String {
        return "error id=[$id] message=[$message]"
    }
}
