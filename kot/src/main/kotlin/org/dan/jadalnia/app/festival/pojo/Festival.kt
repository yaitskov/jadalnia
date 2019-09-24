package org.dan.jadalnia.app.festival.pojo;


import org.dan.jadalnia.app.order.pojo.OrderLabel
import org.dan.jadalnia.app.user.Uid

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

class Festival(
        val info: AtomicReference<FestivalInfo>,
        val readyToExecOrders: LinkedHashMap<OrderLabel, Unit>,
        val freeKelners: ConcurrentHashMap<Uid, Uid>,
        val nextLabel: AtomicInteger) {

    fun fid() = info.get().fid
}
