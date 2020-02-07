package org.dan.jadalnia.app.order;

import lombok.val;
import org.dan.jadalnia.app.festival.menu.DishName;
import org.dan.jadalnia.app.user.UserSession;
import org.dan.jadalnia.mock.MyRest;
import org.dan.jadalnia.test.ws.WsIntegrationTest;
import org.hamcrest.core.Is;
import org.junit.Test;

import java.util.Collections;
import java.util.Optional;

import static java.util.Collections.emptyMap;
import static org.dan.jadalnia.app.festival.SetMenuTest.FRYTKI;
import static org.dan.jadalnia.app.order.CustomerNotifiedAboutOrderExecutingTest.tryExecOrder;
import static org.dan.jadalnia.app.order.CustomerNotifiedThatOrderIsDelayedTest.listDelayedDishes;
import static org.dan.jadalnia.app.order.CustomerNotifiedThatOrderIsDelayedTest.markAsDelayed;
import static org.junit.Assert.assertThat;


public class KelnerUnmarksOrdersDelayedTest extends WsIntegrationTest {
    public static int markAsAvailable(
            MyRest myRest, DishName dishName, UserSession session) {
        return myRest.post(OrderResource.ORDER + "meal-available/" + dishName,
                session, emptyMap(), Integer.class);
    }

    @Test
    public void kelnerMarksOrderAsDelayed() {
        val festState = MockBaseFestival.create(myRest());
        val orderLabel = CustomerPaysForHisOrderTest
                .createPaidOrder(festState, getWsClient());
        val customerSession = festState.sessions.customer;
        val kelnerSession = festState.sessions.kelner;

        assertThat(
                tryExecOrder(myRest(), kelnerSession),
                Is.is(Optional.of(orderLabel)));

        assertThat(
                markAsDelayed(myRest(), orderLabel, FRYTKI, kelnerSession),
                Is.is(true));

        assertThat(
                markAsAvailable(myRest(), FRYTKI, kelnerSession),
                Is.is(1));

        assertThat(
                tryExecOrder(myRest(), kelnerSession),
                Is.is(Optional.of(orderLabel)));

        assertThat(listDelayedDishes(myRest(), customerSession),
                Is.is(Collections.emptyList()));
    }
}
