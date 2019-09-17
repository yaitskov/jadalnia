package org.dan.jadalnia.app.ws

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes(value = [
    JsonSubTypes.Type(
            value = PropertyUpdated::class,
            name = "propertyUpdated")
])
interface MessageForClient
