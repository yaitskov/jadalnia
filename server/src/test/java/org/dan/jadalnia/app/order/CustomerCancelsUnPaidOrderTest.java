package org.dan.jadalnia.app.order;

import lombok.SneakyThrows;
import lombok.val;
import org.dan.jadalnia.app.token.TokenBalanceView;
import org.dan.jadalnia.app.token.TokenPoints;
import org.dan.jadalnia.test.ws.WsIntegrationTest;
import org.hamcrest.core.Is;
import org.junit.Test;

import static org.dan.jadalnia.app.order.CustomerCancelsPaidOrderTest.tryCancelOrder;
import static org.dan.jadalnia.app.order.CustomerPutsOrderTest.putOrder;
import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.FRYTKI_ORDER;
import static org.dan.jadalnia.app.token.CustomerBalanceZeroAtStartTest.getBalance;
import static org.junit.Assert.assertThat;

public class CustomerCancelsUnPaidOrderTest extends WsIntegrationTest {
    @Test
    @SneakyThrows
    public void customerPaysForHisOrder() {
        val festState = MockBaseFestival.create(myRest());
        val customerSession = festState.sessions.customer;
        val unpaidOrderLabel = putOrder(myRest(), customerSession, FRYTKI_ORDER);
        assertThat(tryCancelOrder(myRest(), customerSession, unpaidOrderLabel),
                Is.is(CancelAttemptOutcome.CANCELLED));
        assertThat(getBalance(festState.myRest, customerSession),
                Is.is(new TokenBalanceView(
                        new TokenPoints(0),
                        new TokenPoints(0))));
    }
}
