package org.dan.jadalnia.app.token;

import lombok.SneakyThrows;
import lombok.val;
import org.dan.jadalnia.app.order.MockBaseFestival;
import org.dan.jadalnia.app.user.UserSession;
import org.dan.jadalnia.mock.MyRest;
import org.dan.jadalnia.test.ws.WsIntegrationTest;
import org.hamcrest.core.Is;
import org.junit.Test;

import static java.util.Collections.singletonList;
import static org.dan.jadalnia.app.token.CustomerBalanceZeroAtStartTest.getBalance;
import static org.dan.jadalnia.app.token.CustomerNotifiedAboutApprovedTokenTest.approveToken;
import static org.dan.jadalnia.app.token.CustomerNotifiedAboutApprovedTokenTest.requestToken;
import static org.junit.Assert.assertThat;

public class CustomerReturnTokensTest extends WsIntegrationTest {
    public static TokenId requestTokenReturn(
            MyRest myRest, TokenPoints points, UserSession session) {
        return myRest.post(TokenResource.REQUEST_TOKEN_RETURN,
                session, points, TokenId.class);
    }

    @Test
    @SneakyThrows
    public void returnAllAfterApprove() {
        val rest = myRest();
        val festival = MockBaseFestival.create(rest);
        val customer = festival.getSessions().getCustomer();
        val cashier = festival.getSessions().getCashier();

        val pointsInToken = TokenPoints.valueOf(20);
        val tokenId = requestToken(rest, pointsInToken, customer);

        approveToken(rest, customer.getUid(), singletonList(tokenId),
                cashier);

        assertThat(getBalance(rest, customer),
                Is.is(new TokenBalanceView(
                        new TokenPoints(20),
                        new TokenPoints(20))));

        val returnTokenId = requestTokenReturn(rest, pointsInToken, customer);

        assertThat(getBalance(rest, customer),
                Is.is(new TokenBalanceView(
                        new TokenPoints(0),
                        new TokenPoints(20))));

        approveToken(rest, customer.getUid(), singletonList(returnTokenId),
                cashier);

        assertThat(getBalance(rest, customer),
                Is.is(new TokenBalanceView(
                        new TokenPoints(0),
                        new TokenPoints(0))));
    }
}
