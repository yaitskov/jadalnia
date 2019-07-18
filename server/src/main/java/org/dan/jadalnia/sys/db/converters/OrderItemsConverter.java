package org.dan.jadalnia.sys.db.converters;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dan.jadalnia.app.festival.OrderItem;

import java.util.List;

public class OrderItemsConverter extends JsonConverter<List<OrderItem>> {
    public OrderItemsConverter() {
        super(new TypeReference<List<OrderItem>>() {}, new ObjectMapper());
    }
}
