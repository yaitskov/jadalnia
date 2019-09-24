package org.dan.jadalnia.app.order

import org.dan.jadalnia.app.festival.pojo.Festival
import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.label.LabelService
import org.dan.jadalnia.app.order.pojo.MarkOrderPaid
import org.dan.jadalnia.app.order.pojo.OrderItem
import org.dan.jadalnia.app.order.pojo.OrderLabel
import org.dan.jadalnia.app.order.pojo.OrderMem
import org.dan.jadalnia.app.order.pojo.OrderState
import org.dan.jadalnia.app.order.pojo.OrderState.Accepted
import org.dan.jadalnia.app.order.pojo.OrderState.Paid
import org.dan.jadalnia.app.user.UserSession
import org.dan.jadalnia.app.ws.WsBroadcast
import org.dan.jadalnia.sys.db.DaoUpdater
import org.dan.jadalnia.sys.error.JadEx.Companion.badRequest
import org.dan.jadalnia.util.collection.AsyncCache
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

class OrderService @Inject constructor(
        val wsBroadcast: WsBroadcast,
        val orderCacheByLabel: AsyncCache<Pair<Fid, OrderLabel>, OrderMem>,
        val orderDao: OrderDao,
        val daoUpdater: DaoUpdater,
        val labelService: LabelService) {

    fun putNewOrder(
            festival: Festival,
            customerSession: UserSession,
            newOrderItems: List<OrderItem>): CompletableFuture<OrderLabel> {
        return labelService
                .allocate(festival)
                .thenApply { label -> orderCacheByLabel.inject(
                        Pair(festival.fid(), label),
                        OrderMem(
                                label = label,
                                customer = customerSession.uid,
                                items = newOrderItems,
                                state = AtomicReference(Accepted)
                        )) }
                .thenCompose { order -> orderDao.storeNewOrder(festival.fid(), order) }
    }

    fun markOrderPaid(festival: Festival, paidOrder: MarkOrderPaid)
            : CompletableFuture<Boolean> {
        return orderCacheByLabel.get(key(festival, paidOrder.label))
                .thenApply { order ->
                    val stWas = order.state.getAndUpdate { st ->
                        when (st) {
                            Paid -> Paid
                            Accepted -> Paid
                            else -> throw badRequest(
                                    "Order cannot be paid", "label", order.label)
                        }
                    }
                    if (stWas == Accepted) {
                        festival.readyToExecOrders[order.label] = Unit
                        wsBroadcast.broadcastToFreeKelners(
                                festival, OrderPaidEvent(order.label))
                        daoUpdater.exec { orderDao.updateState(festival.fid(), order.label, Paid) }
                        true
                    } else {
                        false
                    }
                }
    }

    private fun key(festival: Festival, label: OrderLabel) = Pair(festival.fid(), label)
}
