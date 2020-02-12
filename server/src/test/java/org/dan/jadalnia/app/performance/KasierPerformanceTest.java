package org.dan.jadalnia.app.performance;

import lombok.SneakyThrows;
import lombok.val;
import org.dan.jadalnia.app.festival.pojo.Fid;
import org.dan.jadalnia.app.order.CustomerPaysForHisOrderTest;
import org.dan.jadalnia.app.order.MockBaseFestival;
import org.dan.jadalnia.app.order.pref.CashierPerformanceRow;
import org.dan.jadalnia.app.token.TokenPoints;
import org.dan.jadalnia.mock.MyRest;
import org.dan.jadalnia.test.ws.WsIntegrationTest;
import org.hamcrest.core.Is;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.dan.jadalnia.app.token.CustomerNotifiedAboutApprovedTokenTest.approveToken;
import static org.dan.jadalnia.app.token.CustomerNotifiedAboutApprovedTokenTest.requestToken;
import static org.junit.Assert.assertThat;

public class KasierPerformanceTest extends WsIntegrationTest {
    public static List<CashierPerformanceRow> cashierPerformance(MyRest myRest, Fid fid) {
        return myRest.get("/performance/cashier/" + fid,
                new GenericType<List<CashierPerformanceRow>>() {});
    }

    @Test
    @SneakyThrows
    public void cashierPerformance() {
        val festState = MockBaseFestival.create(myRest());

        val customerSession = festState.getSessions().getCustomer();
        val cashierSession = festState.getSessions().getCashier();

        val pointsInToken = TokenPoints.valueOf(10);
        val tokenId = requestToken(festState.getMyRest(), pointsInToken, customerSession);

        assertThat(
                cashierPerformance(festState.getMyRest(), festState.getFestival().getFid()),
                Is.is(
                        singletonList(
                                new CashierPerformanceRow(
                                        festState.getSessions().getCashierName(),
                                        0,
                                        new TokenPoints(0)))));

        approveToken(festState.getMyRest(), customerSession.getUid(),
                singletonList(tokenId), cashierSession);

        assertThat(
                cashierPerformance(festState.getMyRest(), festState.getFestival().getFid()),
                Is.is(
                        singletonList(
                                new CashierPerformanceRow(
                                        festState.getSessions().getCashierName(),
                                        1,
                                        new TokenPoints(10)))));
    }
}
