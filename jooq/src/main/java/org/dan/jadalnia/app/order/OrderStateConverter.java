package org.dan.jadalnia.app.order;

import org.dan.jadalnia.app.order.pojo.OrderState;
import org.jooq.impl.EnumConverter;

public class OrderStateConverter extends EnumConverter<String, OrderState> {
    public OrderStateConverter() {
        super(String.class, OrderState.class);
    }
}
