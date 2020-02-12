package org.dan.jadalnia.app.performance;

import lombok.SneakyThrows;
import lombok.val;
import org.dan.jadalnia.app.festival.pojo.Fid;
import org.dan.jadalnia.app.order.CustomerPaysForHisOrderTest;
import org.dan.jadalnia.app.order.MockBaseFestival;
import org.dan.jadalnia.app.order.pref.KelnerPerformanceRow;
import org.dan.jadalnia.app.token.TokenPoints;
import org.dan.jadalnia.mock.MyRest;
import org.dan.jadalnia.test.ws.WsIntegrationTest;
import org.hamcrest.core.Is;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.dan.jadalnia.app.order.CustomerNotifiedAboutOrderExecutingTest.tryExecOrder;
import static org.dan.jadalnia.app.order.CustomerNotifiedThatOrderIsReadyTest.markAsReady;
import static org.dan.jadalnia.app.order.CustomerPicksReadyOrderTest.pickReadyOrder;
import static org.junit.Assert.assertThat;

public class KelnerPerformanceTest extends WsIntegrationTest {
    public static List<KelnerPerformanceRow> kelnerPerformance(MyRest myRest, Fid fid) {
        return myRest.get("/performance/kelner/" + fid,
                new GenericType<List<KelnerPerformanceRow>>() {});
    }

    @Test
    @SneakyThrows
    public void kelnerPerformance() {
        val festState = MockBaseFestival.create(myRest());
        val orderLabel = CustomerPaysForHisOrderTest
                .createPaidOrder(festState, getWsClient());

        val customerSession = festState.getSessions().getCustomer();
        val kelnerSession = festState.getSessions().getKelner();

        tryExecOrder(myRest(), kelnerSession);

        assertThat(
                kelnerPerformance(festState.getMyRest(), festState.getFestival().getFid()),
                Is.is(
                        singletonList(
                                new KelnerPerformanceRow(
                                        festState.getSessions().getKelnerName(),
                                        0,
                                        new TokenPoints(0)))));

        assertThat(markAsReady(myRest(), orderLabel, kelnerSession), Is.is(true));

        assertThat(
                kelnerPerformance(festState.getMyRest(), festState.getFestival().getFid()),
                Is.is(
                        singletonList(
                                new KelnerPerformanceRow(
                                        festState.getSessions().getKelnerName(),
                                        1,
                                        new TokenPoints(3)))));

        pickReadyOrder(myRest(), orderLabel, customerSession);

        assertThat(
                kelnerPerformance(festState.getMyRest(), festState.getFestival().getFid()),
                Is.is(
                        singletonList(
                                new KelnerPerformanceRow(
                                        festState.getSessions().getKelnerName(),
                                        1,
                                        new TokenPoints(3)))));
    }
}
