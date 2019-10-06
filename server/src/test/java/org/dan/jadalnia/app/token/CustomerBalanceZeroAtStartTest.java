package org.dan.jadalnia.app.token;

import lombok.SneakyThrows;
import lombok.val;
import org.dan.jadalnia.app.user.UserSession;
import org.dan.jadalnia.mock.MyRest;
import org.dan.jadalnia.test.ws.WsIntegrationTest;
import org.hamcrest.core.Is;
import org.junit.Test;

import static org.dan.jadalnia.app.festival.NewFestivalTest.createFestival;
import static org.dan.jadalnia.app.festival.NewFestivalTest.genAdminKey;
import static org.dan.jadalnia.app.user.CustomerGetsFestivalStatusOnConnectTest.genUserKey;
import static org.dan.jadalnia.app.user.CustomerGetsFestivalStatusOnConnectTest.registerCustomer;
import static org.junit.Assert.assertThat;

public class CustomerBalanceZeroAtStartTest extends WsIntegrationTest {
    public static TokenBalanceView getBalance(
            MyRest myRest, UserSession customerSession) {
        return myRest.get(TokenResource.GET_MY_BALANCE,
                customerSession, TokenBalanceView.class);
    }

    @Test
    @SneakyThrows
    public void balanceZero() {
        val festival = createFestival(genAdminKey(), myRest());

        val customerSession = registerCustomer(
                festival.getFid(), genUserKey(), myRest());
        assertThat(getBalance(myRest(), customerSession),
                Is.is(new TokenBalanceView(
                        new TokenPoints(0),
                        new TokenPoints(0))));
    }
}
