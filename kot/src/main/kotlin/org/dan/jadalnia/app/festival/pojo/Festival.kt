package org.dan.jadalnia.app.festival.pojo;

import com.google.common.util.concurrent.AtomicLongMap;
import org.dan.jadalnia.app.festival.menu.DishName;
import org.dan.jadalnia.app.order.pojo.Oid;
import org.dan.jadalnia.app.order.pojo.PaidOrder;
import org.dan.jadalnia.app.user.Uid;

import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

class Festival(
        val info: AtomicReference<FestivalInfo>,
        val paidOrders: LinkedHashMap<Oid, PaidOrder>,
        val kelnersProcessingOrders: MutableMap<Oid, Uid>,
        val requiredItems: AtomicLongMap<DishName>,
        val nextLabel: AtomicInteger) {

    fun fid() = info.get().fid
}
