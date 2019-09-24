package org.dan.jadalnia.app.order


import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.label.AsyncDao
import org.dan.jadalnia.app.order.pojo.OrderLabel
import org.dan.jadalnia.app.order.pojo.OrderMem
import org.dan.jadalnia.app.order.pojo.OrderState
import org.dan.jadalnia.jooq.Tables.ORDERS
import org.slf4j.LoggerFactory
import java.util.*
import java.util.Optional.ofNullable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicReference
import java.util.stream.Collectors.toMap
import kotlin.collections.LinkedHashMap


class OrderDao: AsyncDao() {
    companion object {
        val log = LoggerFactory.getLogger(OrderDao::class.java)
    }

    fun loadReadyToExecOrders(fid: Fid): CompletableFuture<LinkedHashMap<OrderLabel, Unit>> {
        return execQuery {
            jooq -> jooq.select(ORDERS.LABEL)
                    .from(ORDERS)
                    .where(ORDERS.FESTIVAL_ID.eq(fid),
                            ORDERS.STATE.eq(OrderState.Paid))
                    .orderBy(ORDERS.OID)
                    .stream()
                    .map { r -> r.get(ORDERS.LABEL) }
                    .collect(toMap({ o -> o }, { Unit },
                            { _, _ -> throw IllegalStateException() },
                            { -> LinkedHashMap<OrderLabel, Unit>() }))
        }
    }

    fun storeNewOrder(fid: Fid, order: OrderMem):
            CompletableFuture<OrderLabel>  {
        return execQuery { jooq ->
            jooq.insertInto(ORDERS,
                    ORDERS.FESTIVAL_ID,
                    ORDERS.CUSTOMER_ID,
                    ORDERS.LABEL,
                    ORDERS.STATE,
                    ORDERS.REQUIREMENTS)
                    .values(fid, order.customer, order.label,
                            order.state.get(), order.items)
                    .returning(ORDERS.OID)
                    .fetchOne()
                    .getOid() }
                .thenApply { oid ->
                    log.info("Store new order {} => {}", oid, order.label)
                    order.label
                }
    }

    fun updateState(fid: Fid, label: OrderLabel, paid: OrderState): CompletableFuture<Unit> {
        return execQuery { jooq ->
            jooq.update(ORDERS)
                    .set(ORDERS.STATE, paid)
                    .where(ORDERS.FESTIVAL_ID.eq(fid), ORDERS.LABEL.eq(label))
                    .execute() }
                .thenApply { updated ->
                    log.info("Order {}:{} changed paid status ({})", fid, label, updated)
                }
    }

    fun load(fid: Fid, label: OrderLabel): CompletableFuture<Optional<OrderMem>> {
        return execQuery { jooq ->
            ofNullable(jooq.select()
                    .from(ORDERS)
                    .where(ORDERS.FESTIVAL_ID.eq(fid),
                            ORDERS.LABEL.eq(label))
                    .fetchOne())
                    .map { record ->
                        OrderMem(
                              customer = record.get(ORDERS.CUSTOMER_ID),
                                state = AtomicReference(record.get(ORDERS.STATE)),
                                label = label,
                                items = record.get(ORDERS.REQUIREMENTS)
                        )
                    }
        }
    }
}
