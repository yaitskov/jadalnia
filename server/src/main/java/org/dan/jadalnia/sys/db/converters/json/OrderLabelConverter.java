package org.dan.jadalnia.sys.db.converters.json;

import org.dan.jadalnia.app.order.pojo.OrderLabel;
import org.jooq.Converter;

public class OrderLabelConverter implements Converter<Integer, OrderLabel> {
    @Override
    public OrderLabel from(Integer s) {
        if (s == null) {
            return null;
        }
        return OrderLabel.of(s);
    }

    @Override
    public Integer to(OrderLabel l) {
        if (l == null) {
            return null;
        }
        return l.getId();
    }

    @Override
    public Class<Integer> fromType() {
        return Integer.class;
    }

    @Override
    public Class<OrderLabel> toType() {
        return OrderLabel.class;
    }
}
