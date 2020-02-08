package org.dan.jadalnia.app.order;

import lombok.val;
import org.dan.jadalnia.app.festival.menu.DishName;
import org.dan.jadalnia.app.order.pojo.OrderLabel;
import org.dan.jadalnia.app.token.TokenBalanceView;
import org.dan.jadalnia.app.token.TokenPoints;
import org.dan.jadalnia.app.user.UserSession;
import org.dan.jadalnia.test.ws.WsIntegrationTest;
import org.hamcrest.core.Is;
import org.junit.Test;

import static org.dan.jadalnia.app.festival.SetMenuTest.FRYTKI;
import static org.dan.jadalnia.app.order.CustomerEstimatesOrderPriceTest.getCustomerOrderInfo;
import static org.dan.jadalnia.app.order.CustomerModifiesUnPaidOrderTest.tryModifyOrder;
import static org.dan.jadalnia.app.order.CustomerNotifiedAboutOrderExecutingTest.getKelnerOrderInfo;
import static org.dan.jadalnia.app.order.CustomerPaysForHisOrderTest.createPaidOrder;
import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.newFrytkiOrder;
import static org.dan.jadalnia.app.token.CustomerBalanceZeroAtStartTest.getBalance;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;

public class CustomerModifiesPaidOrderTest extends WsIntegrationTest {
    @Test
    public void customerReduceOrderPrice() {
        val festState = MockBaseFestival.create(myRest());
        val customerSession = festState.sessions.customer;
        val orderLabel = createPaidOrder(festState, getWsClient(), newFrytkiOrder(2));

        assertThat(tryModifyOrder(myRest(), customerSession,
                new OrderUpdate(orderLabel, newFrytkiOrder(1))),
                Is.is(UpdateAttemptOutcome.UPDATED));

        checkState(festState, customerSession, orderLabel, 3, 1, 7);
    }

    @Test
    public void customerIncreaseOrderPrice() {
        val festState = MockBaseFestival.create(myRest());
        val customerSession = festState.sessions.customer;
        val orderLabel = createPaidOrder(festState, getWsClient(), newFrytkiOrder(2));

        assertThat(tryModifyOrder(myRest(), customerSession,
                new OrderUpdate(orderLabel, newFrytkiOrder(3))),
                Is.is(UpdateAttemptOutcome.UPDATED));

        checkState(festState, customerSession, orderLabel, 9, 3, 1);
    }

    @Test
    public void customerKeepOrderPrice() {
        val festState = MockBaseFestival.create(myRest());
        val customerSession = festState.sessions.customer;
        val orderLabel = createPaidOrder(festState, getWsClient(), newFrytkiOrder(2));

        assertThat(tryModifyOrder(myRest(), customerSession,
                new OrderUpdate(orderLabel, newFrytkiOrder(2))),
                Is.is(UpdateAttemptOutcome.UPDATED));

        checkStateUnchanged(festState, customerSession, orderLabel);
    }

    @Test
    public void customerExceedsBudget() {
        val festState = MockBaseFestival.create(myRest());
        val customerSession = festState.sessions.customer;
        val orderLabel = createPaidOrder(festState, getWsClient(), newFrytkiOrder(2));

        assertThat(tryModifyOrder(myRest(), customerSession,
                new OrderUpdate(orderLabel, newFrytkiOrder(4))),
                Is.is(UpdateAttemptOutcome.NOT_ENOUGH_FUNDS));

        checkStateUnchanged(festState, customerSession, orderLabel);
    }

    static void checkStateUnchanged(
            MockBaseFestival festState,
            UserSession customerSession,
            OrderLabel orderLabel) {
        checkState(festState, customerSession, orderLabel, 6, 2, 4);

    }

    static void checkState(
            MockBaseFestival festState,
            UserSession customerSession,
            OrderLabel orderLabel,
            int price, int quantity, int balance) {
        checkState(festState, customerSession, orderLabel,
                price, quantity, balance, FRYTKI);
    }

    static void checkState(
            MockBaseFestival festState,
            UserSession customerSession,
            OrderLabel orderLabel,
            int price, int quantity, int balance, DishName dishName) {
        assertThat(
                getCustomerOrderInfo(festState.myRest, customerSession, orderLabel),
                Is.is(allOf(
                        hasProperty("label", Is.is(orderLabel)),
                        hasProperty("price", Is.is(TokenPoints.valueOf(price))))));

        assertThat(getKelnerOrderInfo(festState.myRest, festState.sessions.kelner, orderLabel),
                Is.is(hasProperty("items",
                        hasItem(
                                allOf(
                                        hasProperty("name", Is.is(dishName)),
                                        hasProperty("quantity", Is.is(quantity)))))));

        assertThat(getBalance(festState.myRest, customerSession),
                Is.is(new TokenBalanceView(
                        new TokenPoints(balance),
                        new TokenPoints(balance))));
    }
}
