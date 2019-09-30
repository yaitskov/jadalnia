package org.dan.jadalnia.app.festival.pojo;


import org.dan.jadalnia.app.order.pojo.OrderLabel
import org.dan.jadalnia.app.user.Uid
import java.util.concurrent.BlockingDeque

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

class Festival(
        val info: AtomicReference<FestivalInfo>,
        val readyToExecOrders: BlockingDeque<OrderLabel>,
        val readyToPickupOrders: ConcurrentHashMap<OrderLabel, Unit>,
        val executingOrders: ConcurrentHashMap<OrderLabel, Uid>,
        val freeKelners: ConcurrentHashMap<Uid, Uid>,
        val busyKelners: ConcurrentHashMap<Uid, OrderLabel>,
        val nextLabel: AtomicInteger,
        val nextToken: AtomicInteger) {

    fun fid() = info.get().fid
}
