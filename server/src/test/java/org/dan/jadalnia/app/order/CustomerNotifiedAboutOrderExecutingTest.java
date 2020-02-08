package org.dan.jadalnia.app.order;


import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import lombok.val;
import org.dan.jadalnia.app.festival.pojo.FestivalState;
import org.dan.jadalnia.app.order.pojo.OrderLabel;
import org.dan.jadalnia.app.order.pojo.OrderState;
import org.dan.jadalnia.app.user.UserSession;
import org.dan.jadalnia.app.user.WsClientHandle;
import org.dan.jadalnia.app.ws.MessageForClient;
import org.dan.jadalnia.mock.MyRest;
import org.dan.jadalnia.test.match.PredicateStateMatcher;
import org.dan.jadalnia.test.ws.WsIntegrationTest;
import org.hamcrest.core.Is;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.dan.jadalnia.app.festival.NewFestivalTest.createFestival;
import static org.dan.jadalnia.app.festival.NewFestivalTest.genAdminKey;
import static org.dan.jadalnia.app.festival.SetFestivalStateTest.setState;
import static org.dan.jadalnia.app.festival.SetMenuTest.setMenu;
import static org.dan.jadalnia.app.order.CustomerPutsOrderTest.putOrder;
import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.FRYTKI_ORDER;
import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.markAsPaid;
import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.registerKasier;
import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.registerKelner;
import static org.dan.jadalnia.app.user.CustomerGetsFestivalStatusOnConnectTest.genUserKey;
import static org.dan.jadalnia.app.user.CustomerGetsFestivalStatusOnConnectTest.registerCustomer;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;

public class CustomerNotifiedAboutOrderExecutingTest extends WsIntegrationTest {
    public static KelnerOrderView getKelnerOrderInfo(MyRest myRest, UserSession session, OrderLabel label) {
        return myRest.get(OrderResource.GET_ORDER + "/" + label.getName(),  session, KelnerOrderView.class);
    }

    public static Optional<OrderLabel> tryExecOrder(
            MyRest myRest, UserSession kelnerSession) {
        return myRest.post0(OrderResource.TRY_ORDER, kelnerSession, new GenericType<Optional<OrderLabel>>() {});
    }

    @Test
    @SneakyThrows
    public void kelnerTakesOrderForExecuting() {
        val festival = createFestival(genAdminKey(), myRest());
        val customerSession = registerCustomer(
                festival.getFid(), genUserKey(), myRest());
        val kasierSession = registerKasier(
                festival.getFid(), genUserKey(), myRest());
        val kelnerSession = registerKelner(
                festival.getFid(), genUserKey(), myRest());

        setState(myRest(), festival.getSession(), FestivalState.Open);
        setMenu(myRest(), festival.getSession());

        val orderLabel = putOrder(myRest(), customerSession, FRYTKI_ORDER);

        markAsPaid(myRest(), orderLabel, kasierSession);

        val wsCustomerHandler = WsClientHandle.wsClientHandle(
                customerSession,
                new PredicateStateMatcher<>(
                        (MessageForClient event) ->
                                event instanceof OrderStateEvent
                                        && ((OrderStateEvent) event).getLabel().equals(orderLabel)
                                        && ((OrderStateEvent) event).getState() == OrderState.Executing,
                        new CompletableFuture<>()),
                new TypeReference<MessageForClient>() {
                });

        bindCustomerWsHandler(wsCustomerHandler);

        assertThat(getKelnerOrderInfo(myRest(), kelnerSession, orderLabel).getItems(),
                hasItem(hasProperty("quantity", Is.is(1))));

        assertThat(tryExecOrder(myRest(), kelnerSession), Is.is(Optional.of(orderLabel)));

        wsCustomerHandler.waitTillMatcherSatisfied();
    }
}
