package org.dan.jadalnia.app.festival;

import com.google.common.util.concurrent.AtomicLongMap;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.dan.jadalnia.app.order.Oid;
import org.dan.jadalnia.app.user.Uid;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Festival {
    public static String FESTIVAL_STATE = "state";
    public static String TID = "fid";

    AtomicReference<FestivalInfo> info;

    LinkedHashMap<Oid, PaidOrder> paidOrders;
    Map<Oid, Uid> kelnersProcessingOrders;

    AtomicLongMap<DishName> requiredItems;
}
