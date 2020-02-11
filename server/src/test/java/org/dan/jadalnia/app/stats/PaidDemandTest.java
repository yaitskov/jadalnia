package org.dan.jadalnia.app.stats;

import lombok.SneakyThrows;
import lombok.val;
import org.dan.jadalnia.app.festival.pojo.Fid;
import org.dan.jadalnia.app.order.MockBaseFestival;
import org.dan.jadalnia.app.order.PaymentAttemptOutcome;
import org.dan.jadalnia.app.order.stats.MealDemand;
import org.dan.jadalnia.app.token.TokenPoints;
import org.dan.jadalnia.mock.MyRest;
import org.dan.jadalnia.test.ws.WsIntegrationTest;
import org.hamcrest.core.Is;
import org.junit.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.dan.jadalnia.app.festival.SetMenuTest.FRYTKI;
import static org.dan.jadalnia.app.order.CustomerPaysForHisOrderTest.tryPayOrder;
import static org.dan.jadalnia.app.order.CustomerPutsOrderTest.putOrder;
import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.FRYTKI_ORDER;
import static org.dan.jadalnia.app.token.CustomerNotifiedAboutApprovedTokenTest.approveToken;
import static org.dan.jadalnia.app.token.CustomerNotifiedAboutApprovedTokenTest.requestToken;
import static org.junit.Assert.assertThat;

public class PaidDemandTest extends WsIntegrationTest {
    public static MealDemand paidDemand(MyRest myRest, Fid fid) {
        return myRest.get("/order-stats/demand/" + fid, MealDemand.class);
    }

    @Test
    @SneakyThrows
    public void paidDemand() {
        val festState = MockBaseFestival.create(myRest());
        val customer = festState.getSessions().getCustomer();
        val rest = festState.getMyRest();

        val o1 = putOrder(rest, customer, FRYTKI_ORDER);

        assertThat(
                paidDemand(rest, festState.getFestival().getFid()),
                Is.is(new MealDemand(emptyMap())));

        val tokenId = requestToken(rest, TokenPoints.valueOf(10), customer);

        approveToken(rest, customer.getUid(),
                asList(tokenId), festState.getSessions().getCashier());

        assertThat(
                tryPayOrder(rest, customer, o1), Is.is(PaymentAttemptOutcome.ORDER_PAID));

        assertThat(
                paidDemand(rest, festState.getFestival().getFid()),
                Is.is(new MealDemand(singletonMap(FRYTKI, 1))));

        assertThat(tryPayOrder(rest, customer, putOrder(rest, customer, FRYTKI_ORDER)),
                Is.is(PaymentAttemptOutcome.ORDER_PAID));

        assertThat(
                paidDemand(rest, festState.getFestival().getFid()),
                Is.is(new MealDemand(singletonMap(FRYTKI, 2))));
    }
}
