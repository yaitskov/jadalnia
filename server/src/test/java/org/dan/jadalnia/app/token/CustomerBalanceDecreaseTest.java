package org.dan.jadalnia.app.token;

import com.google.common.collect.ImmutableMap;
import lombok.SneakyThrows;
import lombok.val;
import org.dan.jadalnia.app.festival.pojo.FestivalState;
import org.dan.jadalnia.app.user.UserSession;
import org.dan.jadalnia.app.user.UserType;
import org.dan.jadalnia.mock.MyRest;
import org.dan.jadalnia.test.ws.WsIntegrationTest;
import org.hamcrest.core.Is;
import org.junit.Test;

import java.util.Map;

import static java.util.Arrays.asList;
import static org.dan.jadalnia.app.festival.NewFestivalTest.createFestival;
import static org.dan.jadalnia.app.festival.NewFestivalTest.genAdminKey;
import static org.dan.jadalnia.app.festival.SetFestivalStateTest.setState;
import static org.dan.jadalnia.app.festival.SetMenuTest.setMenu;
import static org.dan.jadalnia.app.order.CustomerPaysForHisOrderTest.tryPayOrder;
import static org.dan.jadalnia.app.order.CustomerPutsOrderTest.putOrder;
import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.FRYTKI_ORDER;
import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.registerKasier;
import static org.dan.jadalnia.app.token.CustomerBalanceZeroAtStartTest.getBalance;
import static org.dan.jadalnia.app.token.CustomerNotifiedAboutApprovedTokenTest.approveToken;
import static org.dan.jadalnia.app.token.CustomerNotifiedAboutApprovedTokenTest.listPendingTokens;
import static org.dan.jadalnia.app.token.CustomerNotifiedAboutApprovedTokenTest.requestToken;
import static org.dan.jadalnia.app.user.CustomerGetsFestivalStatusOnConnectTest.genUserKey;
import static org.dan.jadalnia.app.user.CustomerGetsFestivalStatusOnConnectTest.registerCustomer;
import static org.junit.Assert.assertThat;

public class CustomerBalanceDecreaseTest extends WsIntegrationTest {
    public static Map<UserType, UserSession> put20Spend3(MyRest myRest) {
        val festival = createFestival(genAdminKey(), myRest);
        setState(myRest, festival.getSession(), FestivalState.Open);
        setMenu(myRest, festival.getSession());

        val customerSession = registerCustomer(
                festival.getFid(), genUserKey(), myRest);
        val kasierSession = registerKasier(
                festival.getFid(), genUserKey(), myRest);

        val pointsInToken = TokenPoints.valueOf(20);
        val tokenId = requestToken(myRest, pointsInToken, customerSession);
        assertThat(
                listPendingTokens(myRest, customerSession.getUid(), kasierSession),
                Is.is(asList(new PreApproveTokenView(tokenId, pointsInToken))));
        assertThat(
                approveToken(myRest, customerSession.getUid(),
                        asList(tokenId), kasierSession),
                Is.is(asList(new PreApproveTokenView(tokenId, pointsInToken))));
        val orderLabel = putOrder(myRest, customerSession, FRYTKI_ORDER);
        tryPayOrder(myRest, customerSession, orderLabel);
        return ImmutableMap.of(UserType.Customer, customerSession);
    }

    @Test
    @SneakyThrows
    public void balanceDecreaseAfterOrderPaid() {
        val sessions =  put20Spend3(myRest());
        assertThat(getBalance(myRest(), sessions.get(UserType.Customer)),
                Is.is(new TokenBalanceView(
                        new TokenPoints(17),
                        new TokenPoints(17))));
    }
}
