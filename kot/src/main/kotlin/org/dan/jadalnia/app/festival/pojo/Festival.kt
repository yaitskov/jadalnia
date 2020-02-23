package org.dan.jadalnia.app.festival.pojo;


import org.dan.jadalnia.app.order.line.OrderExecEstimationState
import org.dan.jadalnia.app.order.pojo.OrderLabel
import org.dan.jadalnia.app.user.Uid
import org.dan.jadalnia.util.collection.MapQ
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

class Festival(
    val info: AtomicReference<FestivalInfo>,
    val readyToExecOrders: MapQ<OrderLabel>,
    val readyToPickupOrders: ConcurrentMap<OrderLabel, Unit>,
    val executingOrders: ConcurrentMap<OrderLabel, Uid>,
    val freeKelners: ConcurrentMap<Uid, FreeKelnerInfo>,
    val busyKelners: ConcurrentMap<Uid, OrderLabel>,
    val nextLabel: AtomicInteger,
    val queuesForMissingMeals: MapOfQueues,
    val estimatorState: OrderExecEstimationState,
    val nextToken: AtomicInteger) {

    fun fid() = info.get().fid
}
