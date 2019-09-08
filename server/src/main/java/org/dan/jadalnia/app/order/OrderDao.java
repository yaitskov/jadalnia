package org.dan.jadalnia.app.order;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.dan.jadalnia.app.festival.pojo.Fid;
import org.dan.jadalnia.app.order.pojo.Oid;
import org.dan.jadalnia.app.order.pojo.OrderItem;
import org.dan.jadalnia.app.order.pojo.OrderLabel;
import org.dan.jadalnia.app.order.pojo.OrderState;
import org.dan.jadalnia.app.order.pojo.PaidOrder;
import org.dan.jadalnia.app.user.Uid;
import org.jooq.DSLContext;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.stream.Collectors.toMap;
import static org.dan.jadalnia.app.order.pojo.OrderState.Paid;
import static org.dan.jadalnia.jooq.Tables.ORDERS;
import static org.dan.jadalnia.sys.ctx.ExecutorCtx.DEFAULT_EXECUTOR;

@Slf4j
@FieldDefaults(makeFinal = true)
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class OrderDao {
    DSLContext jooq;
    @Named(DEFAULT_EXECUTOR)
    ExecutorService executor;

    public CompletableFuture<LinkedHashMap<Oid, PaidOrder>> loadPaid(Fid fid) {
        return supplyAsync(
                () -> jooq.select()
                        .from(ORDERS)
                        .where(ORDERS.FESTIVAL_ID.eq(fid), ORDERS.STATE.eq(Paid))
                        .orderBy(ORDERS.OID)
                        .stream()
                        .map(r -> PaidOrder
                                .builder()
                                .orderNumber(r.get(ORDERS.OID))
                                .orderLabel(r.get(ORDERS.LABEL))
                                .items(r.get(ORDERS.REQUIREMENTS))
                                .build())
                        .collect(toMap(PaidOrder::getOrderNumber, o -> o,
                                (a, b) -> { throw new IllegalStateException(); },
                                LinkedHashMap::new)),
                executor);
    }

    public CompletableFuture<OrderLabel> storeNewOrder(
            Fid fid, Uid uid,
            OrderLabel label, List<OrderItem> items)  {
        return supplyAsync(
                () -> jooq.insertInto(ORDERS,
                        ORDERS.FESTIVAL_ID,
                        ORDERS.CUSTOMER_ID,
                        ORDERS.LABEL,
                        ORDERS.STATE,
                        ORDERS.REQUIREMENTS)
                        .values(fid, uid, label, OrderState.Sent, items)
                        .returning(ORDERS.OID)
                        .fetchOne()
                        .getOid(), executor)
                .thenApply(oid -> {
                    log.info("Store new order {} => {}", oid, label);
                    return label;
                });
    }
}
