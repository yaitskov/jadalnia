package org.dan.jadalnia.app.token;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.assertj.core.internal.bytebuddy.implementation.bytecode.Throw;
import org.dan.jadalnia.app.user.UserSession;
import org.dan.jadalnia.app.user.UserType;
import org.dan.jadalnia.mock.MyRest;
import org.dan.jadalnia.test.ws.WsIntegrationTest;
import org.hamcrest.core.Is;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.dan.jadalnia.app.festival.NewFestivalTest.createFestival;
import static org.dan.jadalnia.app.festival.NewFestivalTest.genAdminKey;
import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.registerKasier;
import static org.dan.jadalnia.app.token.CustomerBalanceDecreaseTest.put20Spend3;
import static org.dan.jadalnia.app.token.CustomerBalanceZeroAtStartTest.getBalance;
import static org.dan.jadalnia.app.token.CustomerNotifiedAboutApprovedTokenTest.approveToken;
import static org.dan.jadalnia.app.token.CustomerNotifiedAboutApprovedTokenTest.requestToken;
import static org.dan.jadalnia.app.user.CustomerGetsFestivalStatusOnConnectTest.genUserKey;
import static org.dan.jadalnia.app.user.CustomerGetsFestivalStatusOnConnectTest.registerCustomer;
import static org.junit.Assert.assertThat;

@Slf4j
public class CustomerBalanceKeepsTest extends WsIntegrationTest {
    public static void invalidateBalanceCache(
            MyRest myRest, UserSession customerSession) {
        myRest.voidPost(TokenResource.INVALIDATE_BALANCE_CACHE,
                customerSession, "");
    }

    @Test
    @SneakyThrows
    public void keepsAfterCacheInvalidation() {
        val sessions = put20Spend3(myRest());

        invalidateBalanceCache(myRest(), sessions.get(UserType.Customer));

        assertThat(getBalance(myRest(), sessions.get(UserType.Customer)),
                Is.is(new TokenBalanceView(
                        new TokenPoints(17),
                        new TokenPoints(17))));
    }
}
