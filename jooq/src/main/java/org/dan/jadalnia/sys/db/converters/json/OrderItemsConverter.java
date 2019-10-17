package org.dan.jadalnia.sys.db.converters.json;

import com.fasterxml.jackson.core.type.TypeReference;
import org.dan.jadalnia.app.order.pojo.OrderItem;
import org.dan.jadalnia.sys.jackson.ObjectMapperFactory;

import java.util.List;

public class OrderItemsConverter extends JsonListConverter<OrderItem> {
    public OrderItemsConverter() {
        super(new TypeReference<List<OrderItem>>() {},
                ObjectMapperFactory.INSTANCE.create());
    }
}
