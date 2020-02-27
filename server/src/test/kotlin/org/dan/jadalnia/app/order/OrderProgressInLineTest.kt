package org.dan.jadalnia.app.order

import assertk.assertThat
import assertk.assertions.isEqualTo
import lombok.SneakyThrows
import org.dan.jadalnia.app.festival.pojo.FestParams
import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.FRYTKI_ORDER
import org.dan.jadalnia.app.order.pojo.OrderLabel
import org.dan.jadalnia.app.order.pojo.OrderState
import org.dan.jadalnia.app.user.UserSession
import org.dan.jadalnia.mock.MyRest
import org.dan.jadalnia.test.ws.WsIntegrationTest

import org.junit.Test
import java.util.*

class OrderProgressInLineTest : WsIntegrationTest() {
  @Test
  @SneakyThrows
  fun firstOrder() {
    val festState = MockBaseFestival.create(myRest())
    val params = getFestParams(myRest(), festState.festival.fid)
        .copy(defaultOrderKeepMs = 60_000)
    setFestParams(myRest(), params, festState.festival.session)

    val orderLabel0 = CustomerPaysForHisOrderTest
        .createPaidOrder(festState, FRYTKI_ORDER)
    assertThat(
        lineProgress(festState.getMyRest(), festState.festival.fid, orderLabel0))
        .isEqualTo(OrderProgress(0, 0, OrderState.Paid))

    val orderLabel1 = CustomerPaysForHisOrderTest
        .createPaidOrder(festState, FRYTKI_ORDER)
    assertThat(
        lineProgress(festState.getMyRest(), festState.festival.fid, orderLabel1))
        .isEqualTo(OrderProgress(1, 2 * 60, OrderState.Paid))

    val orderLabel2 = CustomerPaysForHisOrderTest
        .createPaidOrder(festState, FRYTKI_ORDER)
    assertThat(
        lineProgress(festState.getMyRest(), festState.festival.fid, orderLabel2))
        .isEqualTo(OrderProgress(2, 4 * 60, OrderState.Paid))
    val kelnerSession = festState.sessions.getKelner()

    assertThat(CustomerNotifiedAboutOrderExecutingTest.tryExecOrder(myRest(), kelnerSession))
        .isEqualTo(Optional.of(orderLabel0))
    CustomerNotifiedThatOrderIsReadyTest.markAsReady(myRest(), orderLabel0, kelnerSession)

    assertThat(lineProgress(festState.getMyRest(), festState.festival.fid, orderLabel0))
        .isEqualTo(OrderProgress(-1, -1, OrderState.Paid))
  }

  companion object {
    fun lineProgress(myRest: MyRest, fid: Fid, orderLabel: OrderLabel)
        = myRest.get("/order/progress/$fid/$orderLabel", OrderProgress::class.java)

    fun getFestParams(myRest: MyRest, fid: Fid) =
       myRest.get("/festival/params/$fid", FestParams::class.java)

    fun setFestParams(myRest: MyRest, params: FestParams, session: UserSession)
        = myRest.voidPost("/festival/params", session, params)
  }
}