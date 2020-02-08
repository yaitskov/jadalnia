package org.dan.jadalnia.app.order;

import lombok.SneakyThrows;
import lombok.val;
import org.dan.jadalnia.app.order.pojo.OrderItem;
import org.dan.jadalnia.app.order.pojo.OrderLabel;
import org.dan.jadalnia.app.token.TokenPoints;
import org.dan.jadalnia.app.user.UserSession;
import org.dan.jadalnia.mock.MyRest;
import org.dan.jadalnia.test.ws.WsIntegrationTest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.hamcrest.core.Is;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.dan.jadalnia.app.order.CustomerPutsOrderTest.putOrder;
import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.FESTIVAL_STATUS_COND;
import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.FRYTKI_ORDER;
import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.ORDER_STATUS_COND;
import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.festivalStatusAndOrderPaidWaiter;
import static org.dan.jadalnia.app.order.PaymentAttemptOutcome.ORDER_PAID;
import static org.dan.jadalnia.app.token.CustomerNotifiedAboutApprovedTokenTest.approveToken;
import static org.dan.jadalnia.app.token.CustomerNotifiedAboutApprovedTokenTest.listPendingTokens;
import static org.dan.jadalnia.app.token.CustomerNotifiedAboutApprovedTokenTest.requestToken;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.junit.Assert.assertThat;

public class CustomerPaysForHisOrderTest extends WsIntegrationTest {
     public static PaymentAttemptOutcome tryPayOrder(
            MyRest myRest, UserSession session,
            OrderLabel orderLabel) {
        return myRest.post(
                OrderResource.PAY_ORDER + "/" + orderLabel, session,
                "", PaymentAttemptOutcome.class);
    }
    public static OrderLabel createPaidOrder(
            MockBaseFestival mockBaseFestival,
            WebSocketClient wsClient) {
        return createPaidOrder(mockBaseFestival, wsClient, FRYTKI_ORDER);
    }

    public static OrderLabel createPaidOrder(
            MockBaseFestival mockBaseFestival,
            WebSocketClient wsClient, List<OrderItem> orderItems) {
        val sessions = mockBaseFestival.sessions;
        val customerSession = sessions.customer;
        val kasierSession = sessions.cashier;
        val kelnerSession = sessions.kelner;
        val myRest = mockBaseFestival.myRest;

        val pointsInToken = TokenPoints.valueOf(10);
        val tokenId = requestToken(myRest, pointsInToken, customerSession);
        listPendingTokens(myRest, customerSession.getUid(), kasierSession);
        val approved = approveToken(
                myRest, customerSession.getUid(),
                asList(tokenId), kasierSession);
        assertThat(approved, hasItem(hasProperty("tokenId", Is.is(tokenId))));
        val orderLabel = putOrder(myRest, customerSession, orderItems);
        val wsKelnerHandler = festivalStatusAndOrderPaidWaiter(kelnerSession, orderLabel);
        bindUserWsHandler(wsKelnerHandler, wsClient);

        wsKelnerHandler.waitTillMatcherSatisfied(FESTIVAL_STATUS_COND);

        assertThat(tryPayOrder(myRest, customerSession, orderLabel), Is.is(ORDER_PAID));

        wsKelnerHandler.waitTillMatcherSatisfied(ORDER_STATUS_COND);
        return orderLabel;
    }

    @Test
    @SneakyThrows
    public void customerPaysForHisOrder() {
        val festState = MockBaseFestival.create(myRest());
        createPaidOrder(festState, getWsClient());
    }
}
