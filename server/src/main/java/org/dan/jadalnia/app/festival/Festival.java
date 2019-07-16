package org.dan.jadalnia.app.festival;

import com.google.common.cache.LoadingCache;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.dan.jadalnia.app.order.Oid;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
@Builder
public class Festival {
    public static final String FESTIVAL_STATE = "state";
    public static final String TID = "fid";

    private final Fid fid;
    private String name;
    private FestivalState state;
    private List<MenuItem> menu;
    private Instant opensAt;

    private BlockingQueue<Oid> paidOrders;
    private Map<String, AtomicInteger> requiredItems;
    private final LoadingCache<Oid, CompletableFuture<Order>> orders;
}
