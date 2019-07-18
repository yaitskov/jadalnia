package org.dan.jadalnia.app.order;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.dan.jadalnia.app.festival.Fid;
import org.dan.jadalnia.app.festival.PaidOrder;
import org.dan.jadalnia.app.festival.OrderLabel;
import org.jooq.DSLContext;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.stream.Collectors.toMap;
import static org.dan.jadalnia.app.order.OrderState.Paid;
import static org.dan.jadalnia.jooq.Tables.ORDERS;

@Slf4j
@FieldDefaults(makeFinal = true)
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class OrderDao {
    DSLContext jooq;
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
                                .orderLabel(new OrderLabel(r.get(ORDERS.LABEL)))
                                .items(r.get(ORDERS.REQUIREMENTS))
                                .build())
                        .collect(toMap(PaidOrder::getOrderNumber, o -> o,
                                (a, b) -> { throw new IllegalStateException(); },
                                LinkedHashMap::new)),
                executor);
    }
}
