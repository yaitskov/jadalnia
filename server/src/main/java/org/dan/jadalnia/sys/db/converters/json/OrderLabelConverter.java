package org.dan.jadalnia.sys.db.converters.json;

import org.dan.jadalnia.app.festival.order.pojo.OrderLabel;
import org.jooq.Converter;

public class OrderLabelConverter implements Converter<String, OrderLabel> {
    @Override
    public OrderLabel from(String s) {
        if (s == null) {
            return null;
        }
        return new OrderLabel(s);
    }

    @Override
    public String to(OrderLabel l) {
        if (l == null) {
            return null;
        }
        return l.getName();
    }

    @Override
    public Class<String> fromType() {
        return String.class;
    }

    @Override
    public Class<OrderLabel> toType() {
        return OrderLabel.class;
    }
}
