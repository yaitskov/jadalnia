package org.dan.jadalnia.app.order;

import lombok.val;
import org.dan.jadalnia.app.order.pojo.OrderItem;
import org.dan.jadalnia.app.order.pojo.OrderState;
import org.dan.jadalnia.test.ws.WsIntegrationTest;
import org.hamcrest.core.Is;
import org.junit.Test;

import java.util.Collections;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.dan.jadalnia.app.festival.SetMenuTest.FRYTKI;
import static org.dan.jadalnia.app.festival.SetMenuTest.FRYTKI_SUSZY_MENU;
import static org.dan.jadalnia.app.festival.SetMenuTest.SUSZY;
import static org.dan.jadalnia.app.order.CustomerEstimatesOrderPriceTest.getCustomerOrderInfo;
import static org.dan.jadalnia.app.order.CustomerModifiesPaidOrderTest.checkState;
import static org.dan.jadalnia.app.order.CustomerModifiesUnPaidOrderTest.tryModifyOrder;
import static org.dan.jadalnia.app.order.CustomerNotifiedAboutOrderExecutingTest.tryExecOrder;
import static org.dan.jadalnia.app.order.CustomerNotifiedThatOrderIsDelayedTest.markAsDelayed;
import static org.dan.jadalnia.app.order.CustomerPaysForHisOrderTest.createPaidOrder;
import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.newFrytkiOrder;
import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.newSuszyOrder;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;

public class CustomerModifiesDelayedOrderTest extends WsIntegrationTest {
    @Test
    public void customerRemovesUnavailableDishFromOrder() {
        val festState = MockBaseFestival.create(myRest(), FRYTKI_SUSZY_MENU);
        val customerSession = festState.sessions.customer;
        val orderLabel = createPaidOrder(
                festState, getWsClient(), newFrytkiOrder(2));

        assertThat(
                tryExecOrder(myRest(), festState.sessions.kelner),
                Is.is(Optional.of(orderLabel)));

        assertThat(
                markAsDelayed(festState.myRest, orderLabel,
                        FRYTKI, festState.sessions.kelner),
                Is.is(true));

        assertThat(
                tryExecOrder(myRest(), festState.sessions.kelner),
                Is.is(Optional.empty()));

        assertThat(tryModifyOrder(myRest(), customerSession,
                new OrderUpdate(orderLabel, newSuszyOrder(1))),
                Is.is(UpdateAttemptOutcome.UPDATED));

        assertThat(
                tryExecOrder(myRest(), festState.sessions.kelner),
                Is.is(Optional.of(orderLabel)));

        checkState(festState, customerSession, orderLabel, 2, 1, 8, SUSZY);

        assertThat(
                getCustomerOrderInfo(festState.myRest,
                        customerSession, orderLabel),
                Is.is(hasProperty("state", Is.is(OrderState.Executing))));
    }

    @Test
    public void customerKeepUnavailableDishFromOrder() {
        val festState = MockBaseFestival.create(myRest(), FRYTKI_SUSZY_MENU);
        val customerSession = festState.sessions.customer;
        val orderLabel = createPaidOrder(
                festState, getWsClient(), newFrytkiOrder(2));

        assertThat(
                tryExecOrder(myRest(), festState.sessions.kelner),
                Is.is(Optional.of(orderLabel)));

        assertThat(
                markAsDelayed(festState.myRest, orderLabel,
                        FRYTKI, festState.sessions.kelner),
                Is.is(true));

        assertThat(
                tryExecOrder(myRest(), festState.sessions.kelner),
                Is.is(Optional.empty()));

        assertThat(tryModifyOrder(myRest(), customerSession,
                new OrderUpdate(orderLabel,
                        asList(new OrderItem(SUSZY, 1, Collections.emptyList()),
                                new OrderItem(FRYTKI, 2, Collections.emptyList())))),
                Is.is(UpdateAttemptOutcome.UPDATED));

        assertThat(
                tryExecOrder(myRest(), festState.sessions.kelner),
                Is.is(Optional.empty()));

        assertThat(
                getCustomerOrderInfo(festState.myRest, customerSession, orderLabel),
                Is.is(hasProperty("state", Is.is(OrderState.Delayed))));
    }
}
