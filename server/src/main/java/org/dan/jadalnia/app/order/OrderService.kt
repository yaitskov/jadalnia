package org.dan.jadalnia.app.order

import org.dan.jadalnia.app.festival.FestivalService
import org.dan.jadalnia.app.festival.pojo.Festival
import org.dan.jadalnia.app.label.LabelService
import org.dan.jadalnia.app.order.pojo.OrderItem
import org.dan.jadalnia.app.order.pojo.OrderLabel
import org.dan.jadalnia.app.user.UserSession

import javax.inject.Inject

import java.util.concurrent.CompletableFuture


class OrderService @Inject constructor(
        val festivalService: FestivalService,
        val orderDao: OrderDao,
        val labelService: LabelService) {
    fun putNewOrder(
            festival: Festival,
            customerSession: UserSession,
            newOrderItems: List<OrderItem>)
            : CompletableFuture<OrderLabel> {
        return labelService
                .allocate(festival)
                .thenCompose({ label -> orderDao.storeNewOrder(
                        festival.fid(),
                        customerSession.uid,
                        label,
                        newOrderItems)
                })
    }
}
