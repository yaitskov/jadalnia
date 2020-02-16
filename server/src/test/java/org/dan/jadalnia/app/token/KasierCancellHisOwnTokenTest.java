package org.dan.jadalnia.app.token;

import lombok.SneakyThrows;
import lombok.val;
import org.dan.jadalnia.app.order.MockBaseFestival;
import org.dan.jadalnia.app.user.UserSession;
import org.dan.jadalnia.mock.MyRest;
import org.dan.jadalnia.test.ws.WsIntegrationTest;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import java.util.List;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static org.dan.jadalnia.app.token.CustomerBalanceZeroAtStartTest.getBalance;
import static org.dan.jadalnia.app.token.CustomerNotifiedAboutApprovedTokenTest.approveToken;
import static org.dan.jadalnia.app.token.CustomerNotifiedAboutApprovedTokenTest.requestToken;
import static org.dan.jadalnia.app.token.CustomerReturnTokensTest.requestTokenReturn;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;

public class KasierCancellHisOwnTokenTest extends WsIntegrationTest {
    public static List<KasierHistoryRecord> listKasierHistory(
            MyRest myRest, UserSession session, int page) {
        return myRest.get("/token/request-kasier-history/" + page,
                session,
                new GenericType<List<KasierHistoryRecord>>() {});
    }

    public static TokenRequestCashierView showTokenRequestToKasier(
            MyRest myRest, UserSession session, TokenId tokenId) {
        return myRest.get("/token/cashier-view/" + tokenId,
                session, TokenRequestCashierView.class);
    }

    public static TokenRequestCancelOutcome cancelApprovedToken(
            MyRest myRest, UserSession session, TokenId tokenId) {
        return myRest.post("/token/cancel-approved/" + tokenId,
                session, emptyMap(), TokenRequestCancelOutcome.class);
    }

    @Test
    @SneakyThrows
    public void kasierCancelsHisApprovedTokenRequest() {
        val rest = myRest();
        val festival = MockBaseFestival.create(rest);
        val customer = festival.getSessions().getCustomer();
        val cashier = festival.getSessions().getCashier();

        val pointsInToken = TokenPoints.valueOf(20);
        val tokenId = requestToken(rest, pointsInToken, customer);

        assertThat(cancelApprovedToken(rest, cashier, tokenId),
                Is.is(TokenRequestCancelOutcome.BAD_STATE));

        approveToken(rest, customer.getUid(), singletonList(tokenId),
                cashier);

        assertThat(listKasierHistory(rest, cashier, 0),
                Is.is(singletonList(
                        new KasierHistoryRecord(tokenId, pointsInToken))));

        assertThat(showTokenRequestToKasier(rest, cashier, tokenId),
                allOf(
                        hasProperty("tokenId", Is.is(tokenId)),
                        hasProperty("cancelledBy", Matchers.nullValue()),
                        hasProperty("amount", Is.is(pointsInToken))));

        assertThat(cancelApprovedToken(rest, cashier, tokenId),
                Is.is(TokenRequestCancelOutcome.CANCELLED));

        assertThat(showTokenRequestToKasier(rest, cashier, tokenId),
                allOf(
                        hasProperty("tokenId", Is.is(tokenId)),
                        hasProperty("cancelledBy", Matchers.notNullValue())));

        assertThat(getBalance(rest, customer),
                Is.is(new TokenBalanceView(
                        new TokenPoints(0),
                        new TokenPoints(0))));

        assertThat(cancelApprovedToken(rest, cashier, tokenId),
                Is.is(TokenRequestCancelOutcome.CANCELLED));
    }

    @Test
    public void cancelTokenReturnRequest() {
        val rest = myRest();
        val festival = MockBaseFestival.create(rest);
        val customer = festival.getSessions().getCustomer();
        val cashier = festival.getSessions().getCashier();

        val pointsInToken = TokenPoints.valueOf(20);
        val tokenId = requestToken(rest, pointsInToken, customer);

        approveToken(rest, customer.getUid(), singletonList(tokenId),
                cashier);

        val returnRequestId = requestTokenReturn(rest, pointsInToken, customer);
        assertThat(returnRequestId, Matchers.notNullValue());

        assertThat(getBalance(rest, customer),
                Is.is(new TokenBalanceView(
                        TokenPoints.valueOf(0),
                        pointsInToken)));

        approveToken(rest, customer.getUid(), singletonList(returnRequestId),
                cashier);

        assertThat(getBalance(rest, customer),
                Is.is(new TokenBalanceView(
                        new TokenPoints(0),
                        new TokenPoints(0))));

        assertThat(cancelApprovedToken(rest, cashier, returnRequestId),
                Is.is(TokenRequestCancelOutcome.CANCELLED));

        assertThat(showTokenRequestToKasier(rest, cashier, returnRequestId),
                allOf(
                        hasProperty("tokenId", Is.is(returnRequestId)),
                        hasProperty("cancelledBy", Matchers.notNullValue())));

        assertThat(getBalance(rest, customer),
                Is.is(new TokenBalanceView(
                        pointsInToken,
                        pointsInToken)));
    }

    @Test
    public void cancelCancellationRequest() {
        val rest = myRest();
        val festival = MockBaseFestival.create(rest);
        val customer = festival.getSessions().getCustomer();
        val cashier = festival.getSessions().getCashier();

        val pointsInToken = TokenPoints.valueOf(20);
        val tokenId = requestToken(rest, pointsInToken, customer);

        approveToken(rest, customer.getUid(), singletonList(tokenId),
                cashier);

        val returnRequestId = requestTokenReturn(rest, pointsInToken, customer);
        assertThat(returnRequestId, Matchers.notNullValue());

        assertThat(getBalance(rest, customer),
                Is.is(new TokenBalanceView(
                        new TokenPoints(0),
                        pointsInToken)));

        approveToken(rest, customer.getUid(), singletonList(returnRequestId),
                cashier);

        assertThat(getBalance(rest, customer),
                Is.is(new TokenBalanceView(
                        new TokenPoints(0),
                        new TokenPoints(0))));

        assertThat(cancelApprovedToken(rest, cashier, returnRequestId),
                Is.is(TokenRequestCancelOutcome.CANCELLED));

        val cancelledRequestInfo = showTokenRequestToKasier(rest, cashier, returnRequestId);
        assertThat(cancelledRequestInfo,
                allOf(
                        hasProperty("tokenId", Is.is(returnRequestId)),
                        hasProperty("cancelledBy", Matchers.notNullValue())));

        assertThat(getBalance(rest, customer),
                Is.is(new TokenBalanceView(
                        pointsInToken,
                        pointsInToken)));

        assertThat(cancelApprovedToken(rest, cashier, cancelledRequestInfo.getCancelledBy()),
                Is.is(TokenRequestCancelOutcome.CANCELLED));

        assertThat(getBalance(rest, customer),
                Is.is(new TokenBalanceView(
                        new TokenPoints(0),
                        new TokenPoints(0))));
    }

    @Test
    public void rejectCancelDueNotEnoughFunds() {
        val rest = myRest();
        val festival = MockBaseFestival.create(rest);
        val customer = festival.getSessions().getCustomer();
        val cashier = festival.getSessions().getCashier();

        val pointsInToken = TokenPoints.valueOf(20);
        val tokenId = requestToken(rest, pointsInToken, customer);

        approveToken(rest, customer.getUid(), singletonList(tokenId),
                cashier);

        val returnRequestId = requestTokenReturn(rest, TokenPoints.valueOf(3), customer);
        assertThat(returnRequestId, Matchers.notNullValue());

        approveToken(rest, customer.getUid(), singletonList(returnRequestId), cashier);

        assertThat(cancelApprovedToken(rest, cashier, tokenId),
                Is.is(TokenRequestCancelOutcome.NOT_ENOUGH_FUNDS));
    }
}
