package org.dan.jadalnia.app.order;

import lombok.val;
import org.dan.jadalnia.app.token.TokenBalanceView;
import org.dan.jadalnia.app.token.TokenPoints;
import org.dan.jadalnia.test.ws.WsIntegrationTest;
import org.hamcrest.core.Is;
import org.junit.Test;

import java.util.Optional;

import static org.dan.jadalnia.app.festival.SetMenuTest.FRYTKI;
import static org.dan.jadalnia.app.order.CustomerCancelsPaidOrderTest.tryCancelOrder;
import static org.dan.jadalnia.app.order.CustomerNotifiedAboutOrderExecutingTest.tryExecOrder;
import static org.dan.jadalnia.app.order.CustomerNotifiedThatOrderIsDelayedTest.markAsDelayed;
import static org.dan.jadalnia.app.token.CustomerBalanceZeroAtStartTest.getBalance;
import static org.junit.Assert.assertThat;


public class CustomerCancellesDelayedOrderTest extends WsIntegrationTest {
    @Test
    public void kelnerTriesToExecCancelledOrder() {
        val festState = MockBaseFestival.create(myRest());
        val paidOrderLabel = CustomerPaysForHisOrderTest
                .createPaidOrder(festState, getWsClient());
        val customerSession = festState.sessions.customer;
        val kelnerSession = festState.sessions.kelner;

        assertThat(
                tryExecOrder(myRest(), kelnerSession),
                Is.is(Optional.of(paidOrderLabel)));

        assertThat(
                markAsDelayed(festState.myRest, paidOrderLabel, FRYTKI, kelnerSession),
                Is.is(true));

        assertThat(tryCancelOrder(myRest(), customerSession, paidOrderLabel),
                Is.is(CancelAttemptOutcome.CANCELLED));

        assertThat(getBalance(festState.myRest, customerSession),
                Is.is(new TokenBalanceView(
                        new TokenPoints(10),
                        new TokenPoints(10))));
    }
}
