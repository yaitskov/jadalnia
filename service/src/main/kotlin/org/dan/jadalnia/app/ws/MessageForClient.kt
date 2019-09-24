package org.dan.jadalnia.app.ws

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.dan.jadalnia.app.order.OrderPaidEvent

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes(value = [
    JsonSubTypes.Type(
            value = PropertyUpdated::class,
            name = "propertyUpdated"),
    JsonSubTypes.Type(
            value = OrderPaidEvent::class,
            name = "orderPaid")
])
interface MessageForClient
