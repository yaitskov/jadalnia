package org.dan.jadalnia.app.order

import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.festival.pojo.Taca
import org.dan.jadalnia.app.label.AsyncDao
import org.dan.jadalnia.app.order.pojo.OrderLabel
import org.dan.jadalnia.app.order.pojo.OrderMem
import org.dan.jadalnia.app.order.pojo.OrderState
import org.dan.jadalnia.app.token.TokenPoints
import org.dan.jadalnia.app.user.Uid
import org.dan.jadalnia.jooq.Tables.ORDERS
import org.dan.jadalnia.util.collection.MapQ
import org.dan.jadalnia.util.collection.MapQ.QueueInsertIdx
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.*
import java.util.Optional.ofNullable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.atomic.AtomicReference

class OrderDao : AsyncDao() {
  companion object {
    val log = LoggerFactory.getLogger(OrderDao::class.java)
  }

  fun loadReadyToExecOrders(fid: Fid): CompletableFuture<MapQ<Taca>> {
    return execQuery { jooq ->
      MapQ(jooq
          .select(
              ORDERS.LABEL,
              ORDERS.PAID_AT,
              ORDERS.QUEUE_INSERT_IDX)
          .from(ORDERS)
          .where(ORDERS.FESTIVAL_ID.eq(fid),
              ORDERS.STATE.eq(OrderState.Paid))
          .associateTo(
              ConcurrentHashMap<QueueInsertIdx, Taca>()) { r ->
            Pair(
                r[ORDERS.QUEUE_INSERT_IDX],
                Taca(r[ORDERS.LABEL], r[ORDERS.PAID_AT]))
          })
    }
  }

  fun loadReadyOrders(fid: Fid): CompletableFuture<ConcurrentMap<OrderLabel, Unit>> {
    return execQuery { jooq ->
      jooq.select(ORDERS.LABEL)
          .from(ORDERS)
          .where(ORDERS.FESTIVAL_ID.eq(fid),
              ORDERS.STATE.eq(OrderState.Ready))
          .associateTo(ConcurrentHashMap<OrderLabel, Unit>())
           { r -> Pair(r.get(ORDERS.LABEL), Unit) }
    }
  }

  fun loadExecutingOrders(fid: Fid)
      : CompletableFuture<Map<OrderLabel, Pair<Uid, Instant>>> {
    return execQuery { jooq ->
      jooq.select(ORDERS.LABEL, ORDERS.KELNER_ID, ORDERS.PAID_AT)
          .from(ORDERS)
          .where(ORDERS.FESTIVAL_ID.eq(fid),
              ORDERS.STATE.eq(OrderState.Executing))
          .associate {
            r -> Pair(r.get(ORDERS.LABEL),
              Pair(r.get(ORDERS.KELNER_ID),
                  r.get(ORDERS.PAID_AT)))
          }
    }
  }

  fun storeNewOrder(fid: Fid, order: OrderMem):
      CompletableFuture<OrderLabel> {
    return execQuery { jooq ->
      jooq.insertInto(ORDERS,
          ORDERS.FESTIVAL_ID,
          ORDERS.CUSTOMER_ID,
          ORDERS.LABEL,
          ORDERS.STATE,
          ORDERS.POINTS_COST,
          ORDERS.REQUIREMENTS)
          .values(fid, order.customer, order.label,
              order.state.get(), order.cost.get(), order.items.get())
          .returning(ORDERS.OID)
          .fetchOne()
          .getOid()
    }.thenApply { oid ->
      log.info("Stored new order {}:{} => {}",
          fid, oid, order.label)
      order.label
    }
  }

  fun updateState(fid: Fid, label: OrderLabel, state: OrderState)
      : CompletableFuture<Unit> {
    return execQuery { jooq ->
      jooq.update(ORDERS)
          .set(ORDERS.STATE, state)
          .where(ORDERS.FESTIVAL_ID.eq(fid), ORDERS.LABEL.eq(label))
          .execute()
    }
        .thenApply { updated ->
          log.info("Order {}:{} changed state to {} ({})",
              fid, label, state, updated)
        }
  }

  fun assignKelner(fid: Fid,
                   label: OrderLabel,
                   kelnerUid: Uid?,
                   queueIdx: QueueInsertIdx?,
                   orderState: OrderState): CompletableFuture<Unit> {
    return execQuery { jooq ->
      jooq.update(ORDERS)
          .set(ORDERS.STATE, orderState)
          .set(ORDERS.KELNER_ID, kelnerUid)
          .set(ORDERS.QUEUE_INSERT_IDX, queueIdx)
          .where(ORDERS.FESTIVAL_ID.eq(fid), ORDERS.LABEL.eq(label))
          .execute()
    }
        .thenApply {
          log.info("Order $fid:$label is $orderState by ($kelnerUid)")
        }
  }

  fun assignKelner(fid: Fid,
                   label: OrderLabel,
                   kelnerUid: Uid?,
                   orderState: OrderState): CompletableFuture<Unit> {
    return execQuery { jooq ->
      jooq.update(ORDERS)
          .set(ORDERS.STATE, orderState)
          .set(ORDERS.KELNER_ID, kelnerUid)
          .where(ORDERS.FESTIVAL_ID.eq(fid), ORDERS.LABEL.eq(label))
          .execute()
    }
        .thenApply {
          log.info("Order $fid:$label is $orderState by ($kelnerUid)")
        }
  }

  fun load(fid: Fid, label: OrderLabel): CompletableFuture<Optional<OrderMem>> {
    return execQuery { jooq ->
      ofNullable(jooq.select(
          ORDERS.QUEUE_INSERT_IDX,
          ORDERS.CUSTOMER_ID, ORDERS.STATE,
          ORDERS.POINTS_COST, ORDERS.REQUIREMENTS)
          .from(ORDERS)
          .where(ORDERS.FESTIVAL_ID.eq(fid),
              ORDERS.LABEL.eq(label))
          .fetchOne())
          .map { record ->
            OrderMem(
                customer = record.get(ORDERS.CUSTOMER_ID),
                state = AtomicReference(record.get(ORDERS.STATE)),
                label = label,
                cost = AtomicReference(record.get(ORDERS.POINTS_COST)),
                items = AtomicReference(record.get(ORDERS.REQUIREMENTS)),
                insertQueueIdx = AtomicReference(record.get(ORDERS.QUEUE_INSERT_IDX)))
          }
    }
  }

  fun loadSumOfPaidOrders(fid: Fid, customerUid: Uid)
      : CompletableFuture<TokenPoints> {
    return execQuery { jooq -> ofNullable(jooq
        .select(ORDERS.POINTS_COST.sum()
            .cast(Integer::class.java).`as`("totalSpend"))
        .from(ORDERS)
        .where(ORDERS.FESTIVAL_ID.eq(fid),
            ORDERS.CUSTOMER_ID.eq(customerUid),
            ORDERS.STATE.`in`(listOf(
                OrderState.Paid,
                OrderState.Executing,
                OrderState.Ready,
                OrderState.Handed)))
        .forUpdate()
        .fetchOne())
        .map { r ->
          val result = TokenPoints(r.get("totalSpend", Int::class.java))
          log.info("User {} spended {}", customerUid, result)
          result
        }
        .orElseGet { TokenPoints(0) }
    }
  }

  fun loadUnpaidCustomerOrders(fid: Fid, customerUid: Uid)
      : CompletableFuture<List<OrderLabel>> {
    return execQuery { jooq -> jooq
        .select(ORDERS.LABEL)
        .from(ORDERS)
        .where(ORDERS.FESTIVAL_ID.eq(fid),
            ORDERS.CUSTOMER_ID.eq(customerUid),
            ORDERS.STATE.eq(OrderState.Accepted))
        .forUpdate()
        .fetch()
        .map { r -> r.get(ORDERS.LABEL) }
    }
  }

  fun findOrdersForCustomer(fid: Fid, customerUid: Uid)
      : CompletableFuture<List<OrderItemView>> {
    return execQuery { jooq -> jooq
        .select(ORDERS.CREATED, ORDERS.LABEL, ORDERS.STATE)
        .from(ORDERS)
        .where(ORDERS.FESTIVAL_ID.eq(fid),
            ORDERS.CUSTOMER_ID.eq(customerUid))
        .fetch()
        .map { r -> OrderItemView(
            label = r.get(ORDERS.LABEL),
            created = r.get(ORDERS.CREATED),
            state = r.get(ORDERS.STATE))
        }
    }
  }

  fun updateCostAndItems(fid: Fid, cost: TokenPoints, update: OrderUpdate)
      : CompletableFuture<Int> {
    return execQuery { jooq ->
      jooq.update(ORDERS)
          .set(ORDERS.POINTS_COST, cost)
          .set(ORDERS.REQUIREMENTS, update.newItems)
          .where(ORDERS.FESTIVAL_ID.eq(fid), ORDERS.LABEL.eq(update.label))
          .execute()
    }.thenApply { updated ->
      log.info("Order {}:{} changed items {} and cost {}",
          fid, update.label, update.newItems, cost)
      updated
    }
  }

  fun markPaid(fid: Fid, label: OrderLabel,
               now: Instant,
               queueInsertIdx: QueueInsertIdx)
      : CompletableFuture<Unit> {
    return execQuery { jooq ->
      jooq.update(ORDERS)
          .set(ORDERS.STATE, OrderState.Paid)
          .set(ORDERS.PAID_AT, now)
          .set(ORDERS.QUEUE_INSERT_IDX, queueInsertIdx)
          .where(ORDERS.FESTIVAL_ID.eq(fid), ORDERS.LABEL.eq(label))
          .execute()
    }
        .thenApply { updated ->
          log.info("Order {}:{} changed paid status ({})", fid, label, updated)
        }
  }
}
