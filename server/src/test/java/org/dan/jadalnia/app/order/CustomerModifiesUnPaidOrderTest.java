package org.dan.jadalnia.app.order;

import lombok.SneakyThrows;
import lombok.val;
import org.dan.jadalnia.app.token.TokenPoints;
import org.dan.jadalnia.app.user.UserSession;
import org.dan.jadalnia.mock.MyRest;
import org.dan.jadalnia.test.ws.WsIntegrationTest;
import org.hamcrest.core.Is;
import org.junit.Test;

import static org.dan.jadalnia.app.festival.SetMenuTest.FRYTKI;
import static org.dan.jadalnia.app.order.CustomerEstimatesOrderPriceTest.getCustomerOrderInfo;
import static org.dan.jadalnia.app.order.CustomerNotifiedAboutOrderExecutingTest.getKelnerOrderInfo;
import static org.dan.jadalnia.app.order.CustomerPutsOrderTest.putOrder;
import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.newFrytkiOrder;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;

public class CustomerModifiesUnPaidOrderTest extends WsIntegrationTest {
    public static UpdateAttemptOutcome tryModifyOrder(
            MyRest myRest, UserSession customerSession,
            OrderUpdate orderUpdate) {
        return myRest.post(
                OrderResource.ORDER + "modify", customerSession,
                orderUpdate, UpdateAttemptOutcome.class);
    }

    @Test
    @SneakyThrows
    public void customerModifiesUnPaidOrder() {
        val festState = MockBaseFestival.create(myRest());
        val customerSession = festState.sessions.customer;
        val orderLabel = putOrder(myRest(), customerSession, newFrytkiOrder(2));
        assertThat(tryModifyOrder(myRest(), customerSession,
                new OrderUpdate(orderLabel, newFrytkiOrder(10))),
                Is.is(UpdateAttemptOutcome.UPDATED));

        assertThat(
                getCustomerOrderInfo(festState.myRest, customerSession, orderLabel),
                Is.is(allOf(
                        hasProperty("label", Is.is(orderLabel)),
                        hasProperty("price", Is.is(TokenPoints.valueOf(30))))));

        assertThat(getKelnerOrderInfo(festState.myRest, festState.sessions.kelner, orderLabel),
                Is.is(hasProperty("items",
                        hasItem(
                                allOf(
                                        hasProperty("name", Is.is(FRYTKI)),
                                        hasProperty("quantity", Is.is(10)))))));
    }
}
