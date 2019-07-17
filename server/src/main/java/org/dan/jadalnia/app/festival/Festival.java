package org.dan.jadalnia.app.festival;

import com.google.common.collect.Multimap;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.dan.jadalnia.app.order.Oid;
import org.dan.jadalnia.app.user.Cid;
import org.dan.jadalnia.app.user.Uid;
import org.dan.jadalnia.app.user.UserWsListener;
import org.dan.jadalnia.app.user.customer.CustomerWsListener;
import org.dan.jadalnia.app.user.kelner.KelnerWsListener;
import org.dan.jadalnia.util.collection.AsyncCache;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Festival {
    public static String FESTIVAL_STATE = "state";
    public static String TID = "fid";

    Fid fid;
    String name;
    FestivalState state;
    List<MenuItem> menu;
    Instant opensAt;

    BlockingQueue<Oid> paidOrders;
    Map<String, AtomicInteger> requiredItems;
    AsyncCache<Oid, Order> orders;
    Multimap<Cid, CustomerWsListener> customerListeners;
    Multimap<Oid, OrderWsListener> orderListeners;
    Multimap<Uid, KelnerWsListener> kelnerListeners;
    Multimap<Uid, UserWsListener> userListeners;
    Map<Oid, Cid> orderToCustomerId;
}
