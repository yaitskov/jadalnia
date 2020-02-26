package org.dan.jadalnia.app.order;

import lombok.SneakyThrows;
import lombok.val;
import org.dan.jadalnia.app.festival.pojo.Fid;
import org.dan.jadalnia.app.order.pojo.OrderLabel;
import org.dan.jadalnia.app.order.pojo.OrderState;
import org.dan.jadalnia.mock.MyRest;
import org.dan.jadalnia.test.ws.WsIntegrationTest;
import org.hamcrest.core.Is;
import org.junit.Test;

import java.util.Optional;

import static org.dan.jadalnia.app.order.CustomerNotifiedAboutOrderExecutingTest.tryExecOrder;
import static org.dan.jadalnia.app.order.CustomerNotifiedThatOrderIsReadyTest.markAsReady;
import static org.junit.Assert.assertThat;

public class OrderProgressInLineTest extends WsIntegrationTest {
    public static OrderProgress lineProgress(
            MyRest myRest, Fid fid,
            OrderLabel orderLabel) {
        return myRest.get("/order/progress/" + fid + "/"+ orderLabel,
                OrderProgress.class);
    }

    @Test
    @SneakyThrows
    public void firstOrder() {
        val festState = MockBaseFestival.create(myRest());
        val orderLabel = CustomerPaysForHisOrderTest
                .createPaidOrder(festState, getWsClient());

        assertThat(
                lineProgress(festState.getMyRest(), festState.festival.getFid(), orderLabel),
                Is.is(new OrderProgress(0, 0, OrderState.Paid)));

        val kelnerSession = festState.sessions.getKelner();

        assertThat(tryExecOrder(myRest(), kelnerSession), Is.is(Optional.of(orderLabel)));

        markAsReady(myRest(), orderLabel, kelnerSession);

        assertThat(
                lineProgress(festState.getMyRest(), festState.festival.getFid(), orderLabel),
                Is.is(new OrderProgress(-1, -1, OrderState.Paid)));
    }
}
