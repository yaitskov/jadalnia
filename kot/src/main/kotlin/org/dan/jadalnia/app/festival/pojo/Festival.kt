package org.dan.jadalnia.app.festival.pojo;


import org.dan.jadalnia.app.order.pojo.OrderLabel
import org.dan.jadalnia.app.user.Uid
import java.util.concurrent.BlockingDeque
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

class Festival(
    val info: AtomicReference<FestivalInfo>,
    val readyToExecOrders: BlockingDeque<OrderLabel>,
    val readyToPickupOrders: ConcurrentMap<OrderLabel, Unit>,
    val executingOrders: ConcurrentMap<OrderLabel, Uid>,
    val freeKelners: ConcurrentMap<Uid, Uid>,
    val busyKelners: ConcurrentMap<Uid, OrderLabel>,
    val nextLabel: AtomicInteger,
    val nextToken: AtomicInteger) {

    fun fid() = info.get().fid
}
