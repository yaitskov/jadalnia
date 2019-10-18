package org.dan.jadalnia.app.order

import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.label.AsyncDao
import org.dan.jadalnia.app.order.pojo.OrderLabel
import org.dan.jadalnia.app.order.pojo.OrderMem
import org.dan.jadalnia.app.order.pojo.OrderState
import org.dan.jadalnia.app.token.TokenPoints
import org.dan.jadalnia.app.user.Uid
import org.dan.jadalnia.jooq.Tables.ORDERS
import org.slf4j.LoggerFactory
import java.util.*
import java.util.Optional.ofNullable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicReference
import java.util.stream.Collectors.toList

class OrderDao : AsyncDao() {
  companion object {
    val log = LoggerFactory.getLogger(OrderDao::class.java)
  }

  fun loadReadyToExecOrders(fid: Fid): CompletableFuture<List<OrderLabel>> {
    return execQuery { jooq ->
      jooq.select(ORDERS.LABEL)
          .from(ORDERS)
          .where(ORDERS.FESTIVAL_ID.eq(fid),
              ORDERS.STATE.eq(OrderState.Paid))
          .orderBy(ORDERS.OID)
          .stream()
          .map { r -> r.get(ORDERS.LABEL) }
          .collect(toList())
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
              order.state.get(), order.cost, order.items)
          .returning(ORDERS.OID)
          .fetchOne()
          .getOid()
    }
        .thenApply { oid ->
          log.info("Store new order {} => {}", oid, order.label)
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
          log.info("Order {}:{} changed paid status ({})", fid, label, updated)
        }
  }

  fun assignKelner(fid: Fid, label: OrderLabel, kelnerUid: Uid): CompletableFuture<Unit> {
    return execQuery { jooq ->
      jooq.update(ORDERS)
          .set(ORDERS.STATE, OrderState.Executing)
          .set(ORDERS.KELNER_ID, kelnerUid)
          .where(ORDERS.FESTIVAL_ID.eq(fid), ORDERS.LABEL.eq(label))
          .execute()
    }
        .thenApply {
          log.info("Order {}:{} is executing by ({})", fid, label, kelnerUid)
        }
  }

  fun load(fid: Fid, label: OrderLabel): CompletableFuture<Optional<OrderMem>> {
    return execQuery { jooq ->
      ofNullable(jooq.select(
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
                cost = record.get(ORDERS.POINTS_COST),
                items = record.get(ORDERS.REQUIREMENTS)
            )
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
}
