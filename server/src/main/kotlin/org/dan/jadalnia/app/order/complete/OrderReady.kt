package org.dan.jadalnia.app.order.complete

import org.dan.jadalnia.app.festival.menu.DishName
import org.dan.jadalnia.app.festival.pojo.Festival
import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.festival.pojo.Taca
import org.dan.jadalnia.app.order.DelayedOrderDao
import org.dan.jadalnia.app.order.OpLog
import org.dan.jadalnia.app.order.OrderDao
import org.dan.jadalnia.app.order.OrderStateEvent
import org.dan.jadalnia.app.order.pojo.OrderLabel
import org.dan.jadalnia.app.order.pojo.OrderMem
import org.dan.jadalnia.app.order.pojo.OrderState
import org.dan.jadalnia.app.order.pojo.ProblemOrder
import org.dan.jadalnia.app.ws.WsBroadcast
import org.dan.jadalnia.util.collection.AsyncCache
import org.dan.jadalnia.util.collection.MapQ
import org.dan.jadalnia.util.time.Clocker
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.completedFuture
import javax.inject.Inject

class OrderReady @Inject constructor(
    val delayedOrderDao: DelayedOrderDao,
    clocker: Clocker,
    wsBroadcast: WsBroadcast,
    orderDao: OrderDao,
    orderCacheByLabel: AsyncCache<Pair<Fid, OrderLabel>, OrderMem>)
  : BaseOrderCompleteStrategy(clocker, wsBroadcast, orderDao, orderCacheByLabel) {

  override val targetState: OrderState = OrderState.Ready

  override fun updateTargetState(
      festival: Festival, problemOrder: ProblemOrder, opLog: OpLog, taca: Taca)
      : CompletableFuture<Optional<MapQ.QueueInsertIdx>> {
    val label = problemOrder.label
    festival.readyToPickupOrders[label] = Unit
    opLog.add { festival.readyToPickupOrders.remove(label) }

    if (festival.queuesForMissingMeals.isEmpty()) {
      return completedFuture(Optional.empty())
    }
    val fid = festival.fid()
    return orderCacheByLabel.get(Pair(fid, label))
        .thenCompose { order ->
          val names = order.items.get().map { it.name }
          resumeMeals(festival, names)
        }.thenApply {
          Optional.empty<MapQ.QueueInsertIdx>()
        }
  }

  fun resumeMeals(festival: Festival, meals: List<DishName>): CompletableFuture<Void> {
    if (meals.isEmpty()) {
      return completedFuture(null)
    }
    return resumeOrdersBackward(festival,
        LinkedList(festival.queuesForMissingMeals.takeAll(meals[0])))
        .thenCompose {
          resumeMeals(festival, meals.subList(1, meals.size))
        }
  }

  fun resumeOrdersBackward(festival: Festival, tacy: LinkedList<Taca>)
      : CompletableFuture<Void> {
    if (tacy.isEmpty()) {
      return completedFuture(null)
    }
    val taca = tacy.removeLast()
    val label = taca.label
    val fid = festival.fid()

    return orderCacheByLabel.get(Pair(fid, label))
        .thenCompose { order ->
          if (order.state.compareAndSet(OrderState.Delayed, OrderState.Paid)) {
            log.info("Move delayed order {}:{} to exec queue", fid, taca)
            wsBroadcast.broadcastToFreeKelners(
                festival, OrderStateEvent(label, OrderState.Paid))
            wsBroadcast.notifyCustomers(
                festival.fid(),
                listOf(order.customer),
                OrderStateEvent(label, OrderState.Paid))
            val insertIdx = festival.readyToExecOrders.enqueueHead(taca)
            order.insertQueueIdx.set(insertIdx)
            orderDao.markPaid(festival.fid(), label, taca.paidAt, insertIdx).thenCompose {
              delayedOrderDao.remove(festival.fid(), label).thenCompose {
                resumeOrdersBackward(festival, tacy)
              }
            }
          } else {
            log.info("Drop delayed order {}:{} due not Delayed but state {} ",
                fid, taca, order.state.get())
            resumeOrdersBackward(festival, tacy)
          }
        }
  }

  fun resumeOrdersWithMeal(festival: Festival, meal: DishName)
      : CompletableFuture<Int> {
    val fid = festival.fid()
    val suspendedOrders = festival.queuesForMissingMeals.takeAll(meal)
    val size = suspendedOrders.size
    log.info("Meal {}:{} was blocking {} orders", fid, meal, size)

    return resumeOrdersBackward(festival, LinkedList(suspendedOrders))
        .thenApply { size }
  }
}