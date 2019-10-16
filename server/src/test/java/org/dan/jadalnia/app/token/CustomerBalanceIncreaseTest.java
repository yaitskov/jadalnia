package org.dan.jadalnia.app.token;

import lombok.SneakyThrows;
import lombok.val;
import org.dan.jadalnia.test.ws.WsIntegrationTest;
import org.hamcrest.core.Is;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.dan.jadalnia.app.festival.NewFestivalTest.createFestival;
import static org.dan.jadalnia.app.festival.NewFestivalTest.genAdminKey;
import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.registerKasier;
import static org.dan.jadalnia.app.token.CustomerBalanceZeroAtStartTest.getBalance;
import static org.dan.jadalnia.app.token.CustomerNotifiedAboutApprovedTokenTest.approveToken;
import static org.dan.jadalnia.app.token.CustomerNotifiedAboutApprovedTokenTest.requestToken;
import static org.dan.jadalnia.app.user.CustomerGetsFestivalStatusOnConnectTest.genUserKey;
import static org.dan.jadalnia.app.user.CustomerGetsFestivalStatusOnConnectTest.registerCustomer;
import static org.junit.Assert.assertThat;

public class CustomerBalanceIncreaseTest extends WsIntegrationTest {
    @Test
    @SneakyThrows
    public void pendingBalanceIncreaseAfterTokenRequest() {
        val festival = createFestival(genAdminKey(), myRest());

        val customerSession = registerCustomer(
                festival.getFid(), genUserKey(), myRest());

        val pointsInToken = TokenPoints.valueOf(20);
        requestToken(myRest(), pointsInToken, customerSession);

        assertThat(getBalance(myRest(), customerSession),
                Is.is(new TokenBalanceView(
                        new TokenPoints(20),
                        new TokenPoints(0))));
    }

    @Test
    @SneakyThrows
    public void effectiveBalanceIncreaseAfterTokenRequestApprove() {
        val festival = createFestival(genAdminKey(), myRest());

        val customerSession = registerCustomer(
                festival.getFid(), genUserKey(), myRest());
        val kasierSession = registerKasier(
                festival.getFid(), genUserKey(), myRest());

        val pointsInToken = TokenPoints.valueOf(20);
        val tokenId = requestToken(myRest(), pointsInToken, customerSession);
        approveToken(myRest(), customerSession.getUid(),
                asList(tokenId), kasierSession);

        assertThat(getBalance(myRest(), customerSession),
                Is.is(new TokenBalanceView(
                        new TokenPoints(20),
                        new TokenPoints(20))));
    }
}
