package org.dan.jadalnia.app.order;

import lombok.val;
import org.dan.jadalnia.app.festival.menu.DishName;
import org.dan.jadalnia.app.festival.pojo.FestivalState;
import org.dan.jadalnia.app.order.pojo.OrderItem;
import org.dan.jadalnia.app.order.pojo.OrderLabel;
import org.dan.jadalnia.app.user.UserSession;
import org.dan.jadalnia.mock.MyRest;
import org.dan.jadalnia.test.ws.WsIntegrationTest;
import org.junit.Test;

import java.util.Collections;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.dan.jadalnia.app.festival.NewFestivalTest.createFestival;
import static org.dan.jadalnia.app.festival.NewFestivalTest.genAdminKey;
import static org.dan.jadalnia.app.festival.SetFestivalStateTest.setState;
import static org.dan.jadalnia.app.order.CustomerNotifiedAboutOrderExecutingTest.tryExecOrder;
import static org.dan.jadalnia.app.order.CustomerNotifiedThatOrderIsReadyTest.markAsReady;
import static org.dan.jadalnia.app.order.CustomerPutsOrderTest.putOrder;
import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.markAsPaid;
import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.registerKasier;
import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.registerKelner;
import static org.dan.jadalnia.app.user.CustomerGetsFestivalStatusOnConnectTest.genUserKey;
import static org.dan.jadalnia.app.user.CustomerGetsFestivalStatusOnConnectTest.registerCustomer;


public class CustomerPicksReadyOrderTest extends WsIntegrationTest {
    public static void pickReadyOrder(
            MyRest myRest, OrderLabel order, UserSession session) {
        myRest.voidPost(OrderResource.PICKUP_ORDER, session, order);
    }

    @Test
    public void kelnerMarksOrderAsReadyForPickup() {
        val festival = createFestival(genAdminKey(), myRest());
        val customerSession = registerCustomer(
                festival.getFid(), genUserKey(), myRest());
        val kasierSession = registerKasier(
                festival.getFid(), genUserKey(), myRest());
        val kelnerSession = registerKelner(
                festival.getFid(), genUserKey(), myRest());

        setState(myRest(), festival.getSession(), FestivalState.Open);

        val orderLabel = putOrder(myRest(),
                customerSession,
                singletonList(
                        new OrderItem(
                                new DishName("rzemniaki"),
                                1,
                                Collections.emptyList())));

        markAsPaid(myRest(), orderLabel, kasierSession);

        assertThat(tryExecOrder(myRest(), kelnerSession)).isEqualTo(Optional.of(orderLabel));

        markAsReady(myRest(), orderLabel, kelnerSession);
        pickReadyOrder(myRest(), orderLabel, customerSession);
    }
}
