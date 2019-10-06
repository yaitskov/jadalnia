package org.dan.jadalnia.app.token;

import lombok.SneakyThrows;
import lombok.val;
import org.dan.jadalnia.app.festival.pojo.FestivalState;
import org.dan.jadalnia.app.order.PaymentAttemptOutcome;
import org.dan.jadalnia.test.ws.WsIntegrationTest;
import org.hamcrest.core.Is;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.dan.jadalnia.app.festival.NewFestivalTest.createFestival;
import static org.dan.jadalnia.app.festival.NewFestivalTest.genAdminKey;
import static org.dan.jadalnia.app.festival.SetFestivalStateTest.setState;
import static org.dan.jadalnia.app.festival.SetMenuTest.setMenu;
import static org.dan.jadalnia.app.order.CustomerPaysForHisOrderTest.tryPayOrder;
import static org.dan.jadalnia.app.order.CustomerPutsOrderTest.putOrder;
import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.FRYTKI_ORDER;
import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.orderPaidWaiter;
import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.registerKasier;
import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.registerKelner;
import static org.dan.jadalnia.app.token.CustomerNotifiedAboutApprovedTokenTest.approveToken;
import static org.dan.jadalnia.app.token.CustomerNotifiedAboutApprovedTokenTest.requestToken;
import static org.dan.jadalnia.app.user.CustomerGetsFestivalStatusOnConnectTest.genUserKey;
import static org.dan.jadalnia.app.user.CustomerGetsFestivalStatusOnConnectTest.registerCustomer;
import static org.junit.Assert.assertThat;

public class OrdersPaidOnTokenApproveTest extends WsIntegrationTest {
    @Test
    @SneakyThrows
    public void kelnerApprovesTokenForCustomerWithOpenOrder() {
        val key = genAdminKey();
        val festival = createFestival(key, myRest());
        val kasierSession = registerKasier(
                festival.getFid(), genUserKey(), myRest());
        val customerSession = registerCustomer(
                festival.getFid(), genUserKey(), myRest());
        val kelnerSession = registerKelner(
                festival.getFid(), genUserKey(), myRest());

        setState(myRest(), festival.getSession(), FestivalState.Open);
        setMenu(myRest(), festival.getSession());

        val pointsInToken = TokenPoints.valueOf(10);
        val tokenId = requestToken(myRest(), pointsInToken, customerSession);
        approveToken(myRest(), customerSession.getUid(), asList(tokenId), kasierSession);
        val orderLabel = putOrder(myRest(), customerSession, FRYTKI_ORDER);
        val wsKelnerHandler = orderPaidWaiter(kelnerSession, orderLabel);
        bindUserWsHandler(wsKelnerHandler);

        assertThat(tryPayOrder(myRest(), customerSession, orderLabel), Is.is(PaymentAttemptOutcome.ORDER_PAID));

        wsKelnerHandler.waitTillMatcherSatisfied();
    }
}
