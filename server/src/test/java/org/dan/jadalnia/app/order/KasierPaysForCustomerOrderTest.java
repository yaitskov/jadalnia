package org.dan.jadalnia.app.order;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import lombok.val;
import org.dan.jadalnia.app.festival.pojo.FestivalState;
import org.dan.jadalnia.app.order.pojo.OrderLabel;
import org.dan.jadalnia.app.order.pojo.OrderState;
import org.dan.jadalnia.app.token.TokenPoints;
import org.dan.jadalnia.app.user.Uid;
import org.dan.jadalnia.app.user.UserSession;
import org.dan.jadalnia.app.user.WsClientHandle;
import org.dan.jadalnia.app.ws.MessageForClient;
import org.dan.jadalnia.mock.MyRest;
import org.dan.jadalnia.test.match.PredicateStateMatcher;
import org.dan.jadalnia.test.ws.WsIntegrationTest;
import org.hamcrest.core.Is;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static java.util.Arrays.asList;
import static org.dan.jadalnia.app.festival.NewFestivalTest.createFestival;
import static org.dan.jadalnia.app.festival.NewFestivalTest.genAdminKey;
import static org.dan.jadalnia.app.festival.SetFestivalStateTest.setState;
import static org.dan.jadalnia.app.festival.SetMenuTest.setMenu;
import static org.dan.jadalnia.app.order.CustomerPutsOrderTest.putOrder;
import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.FRYTKI_ORDER;
import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.orderPaidWaiter;
import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.registerKasier;
import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.registerKelner;
import static org.dan.jadalnia.app.token.CustomerNotifiedAboutApprovedTokenTest.approveToken;
import static org.dan.jadalnia.app.token.CustomerNotifiedAboutApprovedTokenTest.requestToken;
import static org.dan.jadalnia.app.user.CustomerGetsFestivalStatusOnConnectTest.genUserKey;
import static org.dan.jadalnia.app.user.CustomerGetsFestivalStatusOnConnectTest.registerCustomer;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.junit.Assert.assertThat;

public class KasierPaysForCustomerOrderTest extends WsIntegrationTest {
     public static List<OrderLabel> kasierTriesToPayOrders(
            MyRest myRest, UserSession kasierSession,
            Uid customerUid) {
        return myRest.post(
                OrderResource.KASIER_PAY_ORDER, Optional.of(kasierSession),
                customerUid, new GenericType<List<OrderLabel>>() {});
    }

    @Test
    @SneakyThrows
    public void kasierPaysForCustomerOrders() {
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

        val orderLabel = putOrder(myRest(), customerSession, FRYTKI_ORDER);

        val wsKelnerHandler = orderPaidWaiter(kelnerSession, orderLabel);
        bindUserWsHandler(wsKelnerHandler);

        val wsCustomerHandler = WsClientHandle.wsClientHandle(
                customerSession,
                new PredicateStateMatcher<>(
                        (MessageForClient event) ->
                                event instanceof OrderStateEvent
                                        && ((OrderStateEvent) event).getLabel().equals(orderLabel)
                                        && ((OrderStateEvent) event).getState() == OrderState.Paid,
                        new CompletableFuture<>()),
                new TypeReference<MessageForClient>() {
                });

        bindCustomerWsHandler(wsCustomerHandler);

        val pointsInToken = TokenPoints.valueOf(10);
        val tokenId = requestToken(myRest(), pointsInToken, customerSession);
        val approved = approveToken(myRest(), customerSession.getUid(), asList(tokenId), kasierSession);
        assertThat(approved, hasItem(hasProperty("tokenId", Is.is(tokenId))));

        assertThat(
                kasierTriesToPayOrders(myRest(), kasierSession, customerSession.getUid()),
                Is.is(asList(orderLabel)));

        wsKelnerHandler.waitTillMatcherSatisfied();
        wsCustomerHandler.waitTillMatcherSatisfied();
    }
}
