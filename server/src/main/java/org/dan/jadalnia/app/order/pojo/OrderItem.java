package org.dan.jadalnia.app.order.pojo;

import lombok.Getter;
import lombok.Setter;
import org.dan.jadalnia.app.festival.menu.DishName;

import java.util.List;

@Getter
@Setter
public class OrderItem {
    private DishName name;
    private int quantity;
    private List<OrderItem> additions;
}
