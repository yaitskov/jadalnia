package org.dan.jadalnia.sys.db.converters.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dan.jadalnia.app.festival.order.pojo.OrderItem;

import java.util.List;

public class OrderItemsConverter extends JsonListConverter<OrderItem> {
    public OrderItemsConverter() {
        super(new TypeReference<List<OrderItem>>() {}, new ObjectMapper());
    }
}
