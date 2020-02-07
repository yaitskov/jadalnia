package org.dan.jadalnia.app.order;

import lombok.val;
import org.dan.jadalnia.app.festival.menu.DishName;
import org.dan.jadalnia.app.order.pojo.OrderLabel;
import org.dan.jadalnia.app.order.pojo.OrderState;
import org.dan.jadalnia.app.user.UserSession;
import org.dan.jadalnia.mock.MyRest;
import org.dan.jadalnia.test.ws.WsIntegrationTest;
import org.hamcrest.core.Is;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyMap;
import static org.dan.jadalnia.app.festival.SetMenuTest.FRYTKI;
import static org.dan.jadalnia.app.order.CustomerNotifiedAboutOrderExecutingTest.tryExecOrder;
import static org.dan.jadalnia.app.order.EventWatchers.orderWatcher;
import static org.junit.Assert.assertThat;


public class CustomerNotifiedThatOrderIsDelayedTest extends WsIntegrationTest {
    public static boolean markAsDelayed(
            MyRest myRest, OrderLabel order,
            DishName dishName, UserSession session) {
        return myRest.post(OrderResource.ORDER + "low-food/" + order + "/" + dishName,
                session, emptyMap(), Boolean.class);
    }

    public static List<DishName> listDelayedDishes(
            MyRest myRest, UserSession session) {
        return myRest.get(
                OrderResource.ORDER + "list-unavailable-meals-with-orders",
                () -> session,
                new GenericType<List<DishName>>(){});
    }

    @Test
    public void kelnerMarksOrderAsDelayed() {
        val festState = MockBaseFestival.create(myRest());
        val paidOrderLabel = CustomerPaysForHisOrderTest
                .createPaidOrder(festState, getWsClient());
        val customerSession = festState.sessions.customer;
        val kelnerSession = festState.sessions.kelner;

        val wsCustomerHandler = orderWatcher(
                customerSession, paidOrderLabel, OrderState.Delayed);
        bindCustomerWsHandler(wsCustomerHandler);

        assertThat(
                tryExecOrder(myRest(), kelnerSession),
                Is.is(Optional.of(paidOrderLabel)));

        assertThat(
                markAsDelayed(festState.myRest, paidOrderLabel, FRYTKI, kelnerSession),
                Is.is(true));

        wsCustomerHandler.waitTillMatcherSatisfied();

        assertThat(listDelayedDishes(festState.myRest, customerSession),
                Is.is(Collections.singletonList(FRYTKI)));
    }
}
