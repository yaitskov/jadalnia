package org.dan.jadalnia.app.festival;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dan.jadalnia.app.order.Oid;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Getter
    @Setter
    public static class Item {
        private DishName name;
        private int quantity;
        private List<Item> additions;
    }

    private Oid orderNumber;
    private OrderLabel orderLabel;
    private List<Item> items;
}
