package org.dan.jadalnia.app.order;

import org.dan.jadalnia.app.order.pojo.OrderCancellationReason;
import org.jooq.impl.EnumConverter;

public class OrderCancellationReasonConverter extends EnumConverter<String, OrderCancellationReason> {
    public OrderCancellationReasonConverter() {
        super(String.class, OrderCancellationReason.class);
    }
}
