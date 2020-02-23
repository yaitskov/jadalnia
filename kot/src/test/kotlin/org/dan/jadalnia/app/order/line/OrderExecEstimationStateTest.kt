package org.dan.jadalnia.app.order.line

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.dan.jadalnia.app.festival.menu.DishName
import org.dan.jadalnia.app.order.line.OrderExecEstimationState.Companion.defaultOrderBookKeepTimeKey
import org.dan.jadalnia.app.order.line.OrderExecEstimationState.Companion.emptyOrder
import org.dan.jadalnia.app.user.Uid
import org.junit.Test
import java.util.*

class OrderExecEstimationStateTest {
  val frytki = DishName.of("frytki")
  val suszy = DishName.of("suszy")

  @Test
  fun zero() {
    val state = OrderExecEstimationState.create(60_000)
    assertThat(state.estimate(setOf(Uid(1)), 1)).isEqualTo(OrderExecEstimate(0))
  }

  @Test
  fun mealKelnerAvgMsHasValuesForAllMeals() {
    val state = OrderExecEstimationState.create(60_000)

    val queueState = state.mealsAgg.getAggOrNew(1).orderMeals2Count

    queueState.put(mapOf(Pair(frytki, 1)), 2L)
    queueState.put(mapOf(Pair(suszy, 3)), 1L)

    state.mealKelnerAvgMs[Pair(mapOf(Pair(frytki, 1)), Optional.of(Uid(1)))] = minutesToMs(7)
    state.mealKelnerAvgMs[Pair(mapOf(Pair(suszy, 3)), Optional.of(Uid(1)))] = minutesToMs(9)

    assertThat(state.estimate(setOf(Uid(1)), 1))
        .isEqualTo(OrderExecEstimate(14 + 9))
  }

  fun minutesToMs(minutes: Int) = minutes * 1000 * 60

  @Test
  fun mealKelnerAvgMsNoValueForKelner() {
    val state = OrderExecEstimationState.create(60_000)

    val queueState = state.mealsAgg.getAggOrNew(1).orderMeals2Count

    queueState.put(mapOf(Pair(frytki, 2)), 1L)

    // ignore
    state.mealKelnerAvgMs[Pair(mapOf(Pair(frytki, 1)), Optional.of(Uid(1)))] = minutesToMs(7)

    state.mealKelnerAvgMs[defaultOrderBookKeepTimeKey] = minutesToMs(9)

    assertThat(state.estimate(setOf(Uid(1)), 1))
        .isEqualTo(OrderExecEstimate(1 * 2 + 9))
  }

  @Test
  fun mealKelnerAvgMsValueSpecificMeal() {
    val state = OrderExecEstimationState.create(60_000)

    val queueState = state.mealsAgg.getAggOrNew(1).orderMeals2Count

    queueState.put(mapOf(Pair(frytki, 2)), 1L)

    // ignore
    state.mealKelnerAvgMs[Pair(mapOf(Pair(frytki, 1)), Optional.of(Uid(1)))] = minutesToMs(7)

    state.mealKelnerAvgMs[defaultOrderBookKeepTimeKey] = minutesToMs(9)
    state.defaultDishTimeMs[frytki] = minutesToMs(11)

    assertThat(state.estimate(setOf(Uid(1)), 1))
        .isEqualTo(OrderExecEstimate(11 * 2 + 9))
  }

  @Test
  fun mealKelnerAvgMs_KelnerSpecificOrderKeepingTime() {
    val state = OrderExecEstimationState.create(60_000)

    val queueState = state.mealsAgg.getAggOrNew(1).orderMeals2Count

    queueState.put(mapOf(Pair(frytki, 2)), 1L)

    // ignore
    state.mealKelnerAvgMs[Pair(mapOf(Pair(frytki, 1)), Optional.of(Uid(1)))] = minutesToMs(7)
    state.mealKelnerAvgMs[Pair(emptyOrder, Optional.of(Uid(1)))] = minutesToMs(13)
    state.defaultDishTimeMs[frytki] = minutesToMs(11)

    assertThat(state.estimate(setOf(Uid(1)), 1))
        .isEqualTo(OrderExecEstimate(11 * 2 + 13))
  }
}