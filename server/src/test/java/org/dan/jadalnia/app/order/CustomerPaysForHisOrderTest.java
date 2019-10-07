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

import static java.util.Arrays.asList;
import static org.dan.jadalnia.app.festival.NewFestivalTest.createFestival;
import static org.dan.jadalnia.app.festival.NewFestivalTest.genAdminKey;
import static org.dan.jadalnia.app.festival.SetFestivalStateTest.setState;
import static org.dan.jadalnia.app.festival.SetMenuTest.setMenu;
import static org.dan.jadalnia.app.order.CustomerPutsOrderTest.putOrder;
import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.FRYTKI_ORDER;
import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.orderPaidWaiter;
import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.registerKasier;
import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.registerKelner;
import static org.dan.jadalnia.app.order.PaymentAttemptOutcome.ORDER_PAID;
import static org.dan.jadalnia.app.token.CustomerNotifiedAboutApprovedTokenTest.approveToken;
import static org.dan.jadalnia.app.token.CustomerNotifiedAboutApprovedTokenTest.listPendingTokens;
import static org.dan.jadalnia.app.token.CustomerNotifiedAboutApprovedTokenTest.requestToken;
import static org.dan.jadalnia.app.user.CustomerGetsFestivalStatusOnConnectTest.genUserKey;
import static org.dan.jadalnia.app.user.CustomerGetsFestivalStatusOnConnectTest.registerCustomer;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.junit.Assert.assertThat;

public class CustomerPaysForHisOrderTest extends WsIntegrationTest {
     public static PaymentAttemptOutcome tryPayOrder(
            MyRest myRest, UserSession session,
            OrderLabel orderLabel) {
        return myRest.post(
                OrderResource.PAY_ORDER, session,
                orderLabel, PaymentAttemptOutcome.class);
    }

    @Test
    @SneakyThrows
    public void customerPaysForHisOrder() {
        val key = genAdminKey();
        val festival = createFestival(key, myRest());
        val kasierSession = registerKasier(
                festival.getFid(), genUserKey(), myRest());
        val customerSession = registerCustomer(
                festival.getFid(), genUserKey(), myRest());
        val kelnerSession = registerKelner(
                festival.getFid(), genUserKey(), myRest());

        setState(myRest(), festival.getSession(), FestivalState.Open);
        setMenu(myRest(), festival.getSession());

        val pointsInToken = TokenPoints.valueOf(10);
        val tokenId = requestToken(myRest(), pointsInToken, customerSession);
        val approved = approveToken(myRest(), customerSession.getUid(), asList(tokenId), kasierSession);
        listPendingTokens(myRest(), customerSession.getUid(), kasierSession);
        assertThat(approved, hasItem(hasProperty("tokenId", Is.is(tokenId))));
        val orderLabel = putOrder(myRest(), customerSession, FRYTKI_ORDER);
        val wsKelnerHandler = orderPaidWaiter(kelnerSession, orderLabel);
        bindUserWsHandler(wsKelnerHandler);

        assertThat(tryPayOrder(myRest(), customerSession, orderLabel), Is.is(ORDER_PAID));

        wsKelnerHandler.waitTillMatcherSatisfied();
    }
}
