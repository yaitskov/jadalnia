package org.dan.jadalnia.app.order;


import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.order.pojo.Oid
import org.dan.jadalnia.app.order.pojo.OrderItem
import org.dan.jadalnia.app.order.pojo.OrderLabel
import org.dan.jadalnia.app.order.pojo.OrderState
import org.dan.jadalnia.app.order.pojo.PaidOrder
import org.dan.jadalnia.app.user.Uid
import org.dan.jadalnia.jooq.Tables.ORDERS
import org.dan.jadalnia.sys.ctx.ExecutorCtx.Companion.DEFAULT_EXECUTOR
import org.jooq.DSLContext
import org.slf4j.LoggerFactory

import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.supplyAsync
import java.util.concurrent.ExecutorService
import java.util.function.Supplier
import java.util.stream.Collectors.toMap
import javax.inject.Inject
import javax.inject.Named


class OrderDao @Inject constructor(
        val jooq: DSLContext,
        @Named(DEFAULT_EXECUTOR)
        val executor: ExecutorService) {

    companion object {
        val log = LoggerFactory.getLogger(OrderDao::class.java)
    }

    fun loadPaid(fid: Fid): CompletableFuture<LinkedHashMap<Oid, PaidOrder>> {
        class SelectOrders : Supplier<LinkedHashMap<Oid, PaidOrder>> {
            override fun get() =
                    jooq.select()
                            .from(ORDERS)
                            .where(ORDERS.FESTIVAL_ID.eq(fid),
                                    ORDERS.STATE.eq(OrderState.Paid))
                            .orderBy(ORDERS.OID)
                            .stream()
                            .map({ r ->
                                PaidOrder(
                                        orderNumber = r.get(ORDERS.OID),
                                        orderLabel = r.get(ORDERS.LABEL),
                                        items = r.get(ORDERS.REQUIREMENTS))
                            })
                            .collect(toMap(PaidOrder::orderNumber, { o -> o },
                                    { a, b -> throw IllegalStateException() },
                                    { -> LinkedHashMap<Oid, PaidOrder>() }))
        }

        return supplyAsync(SelectOrders(), executor)
    }

    fun storeNewOrder(
            fid: Fid, uid: Uid, label: OrderLabel,
            items: List<OrderItem>):
            CompletableFuture<OrderLabel>  {

        class InsertOrder : Supplier<Oid> {
            override fun get(): Oid =
                    jooq.insertInto(ORDERS,
                            ORDERS.FESTIVAL_ID,
                            ORDERS.CUSTOMER_ID,
                            ORDERS.LABEL,
                            ORDERS.STATE,
                            ORDERS.REQUIREMENTS)
                            .values(fid, uid, label, OrderState.Sent, items)
                            .returning(ORDERS.OID)
                            .fetchOne()
                            .getOid()
        }

        return supplyAsync(InsertOrder(), executor)
                .thenApply({ oid ->
                    log.info("Store new order {} => {}", oid, label)
                    label
                })
    }
}
