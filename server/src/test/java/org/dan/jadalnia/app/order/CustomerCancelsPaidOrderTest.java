package org.dan.jadalnia.app.order;

import lombok.SneakyThrows;
import lombok.val;
import org.dan.jadalnia.app.order.pojo.OrderLabel;
import org.dan.jadalnia.app.order.pojo.OrderState;
import org.dan.jadalnia.app.token.TokenBalanceView;
import org.dan.jadalnia.app.token.TokenPoints;
import org.dan.jadalnia.app.user.UserSession;
import org.dan.jadalnia.mock.MyRest;
import org.dan.jadalnia.test.ws.WsIntegrationTest;
import org.hamcrest.core.Is;
import org.junit.Test;

import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.ORDER_STATUS_COND;
import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.festivalStatusAndOrderStatus;
import static org.dan.jadalnia.app.token.CustomerBalanceZeroAtStartTest.getBalance;
import static org.junit.Assert.assertThat;

public class CustomerCancelsPaidOrderTest extends WsIntegrationTest {
     public static CancelAttemptOutcome tryCancelOrder(
            MyRest myRest, UserSession customerSession,
            OrderLabel orderLabel) {
        return myRest.post(
                OrderResource.CANCEL_ORDER + "/" + orderLabel, customerSession,
                "", CancelAttemptOutcome.class);
    }

    @Test
    @SneakyThrows
    public void customerPaysForHisOrder() {
        val festState = MockBaseFestival.create(myRest());
        val paidOrderLabel = CustomerPaysForHisOrderTest
                .createPaidOrder(festState, getWsClient());
        val customerSession = festState.sessions.customer;

        assertThat(tryCancelOrder(myRest(), customerSession, paidOrderLabel),
                Is.is(CancelAttemptOutcome.CANCELLED));

        assertThat(getBalance(festState.myRest, customerSession),
                Is.is(new TokenBalanceView(
                        new TokenPoints(10),
                        new TokenPoints(10))));
    }
}
