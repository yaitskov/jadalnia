package org.dan.jadalnia.app.order;

import lombok.SneakyThrows;
import lombok.val;
import org.dan.jadalnia.app.order.pojo.OrderLabel;
import org.dan.jadalnia.app.order.pojo.OrderState;
import org.dan.jadalnia.app.token.TokenPoints;
import org.dan.jadalnia.app.user.Uid;
import org.dan.jadalnia.app.user.UserSession;
import org.dan.jadalnia.mock.MyRest;
import org.dan.jadalnia.test.ws.WsIntegrationTest;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.dan.jadalnia.app.order.CustomerNotifiedAboutOrderExecutingTest.tryExecOrder;
import static org.dan.jadalnia.app.order.CustomerPaysForHisOrderTest.tryPayOrder;
import static org.dan.jadalnia.app.order.CustomerPutsOrderTest.putOrder;
import static org.dan.jadalnia.app.order.EventWatchers.orderWatcher;
import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.FRYTKI_ORDER;
import static org.dan.jadalnia.app.token.CustomerNotifiedAboutApprovedTokenTest.approveToken;
import static org.dan.jadalnia.app.token.CustomerNotifiedAboutApprovedTokenTest.requestToken;

public class CustomerReschedulesHisOrderTest extends WsIntegrationTest {
     public static PaymentAttemptOutcome reschedule(
            MyRest myRest, UserSession session,
            OrderLabel orderLabel) {
        return myRest.post(
                OrderResource.ORDER + "reschedule/" + orderLabel, session,
                "", PaymentAttemptOutcome.class);
    }

    public static Uid customerIsMissing(
            MyRest myRest, UserSession session,
            OrderLabel orderLabel) {
        return myRest.post(
                OrderResource.ORDER + "customer-missing/" + orderLabel, session,
                "", Uid.class);
    }

    public static OrderLabel buyTokenPutOrderAndPay(MockBaseFestival festState) {
        val pointsInToken = TokenPoints.valueOf(10);
        val tokenId = requestToken(festState.myRest, pointsInToken, festState.sessions.customer);

        approveToken(
                festState.myRest, festState.sessions.customer.getUid(),
                asList(tokenId), festState.sessions.cashier);

        val orderLabel = putOrder(festState.myRest, festState.sessions.customer, FRYTKI_ORDER);
        tryPayOrder(festState.myRest, festState.sessions.customer, orderLabel);

        return orderLabel;
    }

    @Test
    @SneakyThrows
    public void customerReschedulesHisOrder() {
        val festState = MockBaseFestival.create(myRest());
        val orderLabel = buyTokenPutOrderAndPay(festState);

        val watchAbandoned = orderWatcher(festState.sessions.customer, orderLabel, OrderState.Abandoned);
        bindCustomerWsHandler(watchAbandoned);
        val watchPaid = orderWatcher(festState.sessions.kelner, orderLabel, OrderState.Paid);
        bindUserWsHandler(watchPaid);

        tryExecOrder(myRest(), festState.sessions.kelner);
        customerIsMissing(festState.myRest, festState.sessions.kelner, orderLabel);

        watchAbandoned.waitTillMatcherSatisfied();
        reschedule(festState.myRest, festState.sessions.customer, orderLabel);

        watchPaid.waitTillMatcherSatisfied();
    }
}
