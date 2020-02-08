package org.dan.jadalnia.app.order;

import lombok.SneakyThrows;
import lombok.val;
import org.dan.jadalnia.app.festival.pojo.FestivalState;
import org.dan.jadalnia.app.order.pojo.OrderLabel;
import org.dan.jadalnia.app.token.TokenPoints;
import org.dan.jadalnia.app.user.UserSession;
import org.dan.jadalnia.mock.MyRest;
import org.dan.jadalnia.test.ws.WsIntegrationTest;
import org.hamcrest.core.Is;
import org.junit.Test;

import static org.dan.jadalnia.app.festival.NewFestivalTest.createFestival;
import static org.dan.jadalnia.app.festival.NewFestivalTest.genAdminKey;
import static org.dan.jadalnia.app.festival.SetFestivalStateTest.setState;
import static org.dan.jadalnia.app.festival.SetMenuTest.setMenu;
import static org.dan.jadalnia.app.order.CustomerPutsOrderTest.putOrder;
import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.newFrytkiOrder;
import static org.dan.jadalnia.app.user.CustomerGetsFestivalStatusOnConnectTest.genUserKey;
import static org.dan.jadalnia.app.user.CustomerGetsFestivalStatusOnConnectTest.registerCustomer;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;

public class CustomerEstimatesOrderPriceTest extends WsIntegrationTest {
     public static VisitorOrderView getCustomerOrderInfo(
            MyRest myRest, UserSession session,
            OrderLabel label) {
        return myRest.get(OrderResource.ORDER + "customerInfo/" + label,
                session, VisitorOrderView.class);
    }

    @Test
    @SneakyThrows
    public void orderPriceConsistent() {
        val key = genAdminKey();
        val festival = createFestival(key, myRest());
        val customerSession = registerCustomer(
                festival.getFid(), genUserKey(), myRest());
        setState(myRest(), festival.getSession(), FestivalState.Open);
        setMenu(myRest(), festival.getSession());

        val orderLabel = putOrder(myRest(), customerSession, newFrytkiOrder(2));

        assertThat(
                getCustomerOrderInfo(myRest(), customerSession, orderLabel),
                hasProperty("price", Is.is(new TokenPoints(6))));
    }
}
