package org.dan.jadalnia.app.order;

import lombok.val;
import org.dan.jadalnia.test.ws.WsIntegrationTest;
import org.hamcrest.core.Is;
import org.junit.Test;

import java.util.Optional;

import static org.dan.jadalnia.app.order.CustomerCancelsPaidOrderTest.tryCancelOrder;
import static org.dan.jadalnia.app.order.CustomerNotifiedAboutOrderExecutingTest.tryExecOrder;
import static org.junit.Assert.assertThat;


public class KelnerTriesToExecCancelledOrderTest extends WsIntegrationTest {
    @Test
    public void kelnerTriesToExecCancelledOrder() {
        val festState = MockBaseFestival.create(myRest());
        val orderLabel = CustomerPaysForHisOrderTest
                .createPaidOrder(festState, getWsClient());
        val customerSession = festState.sessions.customer;
        assertThat(tryCancelOrder(myRest(), customerSession, orderLabel),
                Is.is(CancelAttemptOutcome.CANCELLED));
        assertThat(
                tryExecOrder(myRest(), festState.sessions.kelner),
                Is.is(Optional.empty()));
    }
}
