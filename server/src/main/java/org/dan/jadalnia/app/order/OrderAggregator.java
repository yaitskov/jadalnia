package org.dan.jadalnia.app.order;

import com.google.common.util.concurrent.AtomicLongMap;
import lombok.val;
import org.dan.jadalnia.app.festival.menu.DishName;
import org.dan.jadalnia.app.order.pojo.PaidOrder;

import java.util.Collection;

public class OrderAggregator {
    public AtomicLongMap<DishName> aggregate(Collection<PaidOrder> paidOrders) {
        val counters = AtomicLongMap.<DishName>create();
        paidOrders.forEach(order -> {
            order.getItems().forEach(item -> {
                counters.addAndGet(item.getName(), item.getQuantity());
                item.getAdditions().forEach(addition ->
                        counters.addAndGet(addition.getName(), addition.getQuantity()));
            });
        });
        return counters;
    }
}
