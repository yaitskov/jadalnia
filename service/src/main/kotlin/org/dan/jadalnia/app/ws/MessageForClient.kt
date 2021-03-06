package org.dan.jadalnia.app.ws

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.dan.jadalnia.app.order.OrderExecutingEvent
import org.dan.jadalnia.app.order.OrderStateEvent
import org.dan.jadalnia.app.token.TokenApprovedEvent

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes(value = [
    JsonSubTypes.Type(
            value = PropertyUpdated::class,
            name = "propertyUpdated"),
    JsonSubTypes.Type(
        value = OrderExecutingEvent::class,
        name = "orderExecuting"),
    JsonSubTypes.Type(
        value = OrderStateEvent::class,
        name = "orderState"),
    JsonSubTypes.Type(
        value = TokenApprovedEvent::class,
        name = "tokenApproved")
])
interface MessageForClient
