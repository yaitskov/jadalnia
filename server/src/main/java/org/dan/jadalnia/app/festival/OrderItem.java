package org.dan.jadalnia.app.festival;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderItem {
    private DishName name;
    private int quantity;
    private List<OrderItem> additions;
}
