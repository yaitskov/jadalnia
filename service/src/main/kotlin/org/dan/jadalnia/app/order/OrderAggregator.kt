package org.dan.jadalnia.app.order;

import com.google.common.util.concurrent.AtomicLongMap
import org.dan.jadalnia.app.festival.menu.DishName
import org.dan.jadalnia.app.order.pojo.PaidOrder

class OrderAggregator {
    fun aggregate(paidOrders: Collection<PaidOrder>): AtomicLongMap<DishName> {
        val counters = AtomicLongMap.create<DishName>()
        paidOrders.forEach({ order ->
            order.items.forEach({ item ->
                counters.addAndGet(item.name, item.quantity.toLong())
                item.additions.forEach({ addition ->
                    counters.addAndGet(addition.name, addition.quantity.toLong())
                })
            })
        })
        return counters;
    }
}
