package org.dan.jadalnia.app.festival.order;

import org.dan.jadalnia.app.festival.order.pojo.OrderState;
import org.jooq.impl.EnumConverter;

public class OrderStateConverter extends EnumConverter<String, OrderState> {
    public OrderStateConverter() {
        super(String.class, OrderState.class);
    }
}
