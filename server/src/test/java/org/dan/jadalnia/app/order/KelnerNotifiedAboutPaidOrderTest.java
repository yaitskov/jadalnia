package org.dan.jadalnia.app.order;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import lombok.val;
import org.dan.jadalnia.app.festival.menu.DishName;
import org.dan.jadalnia.app.festival.pojo.FestivalState;
import org.dan.jadalnia.app.festival.pojo.Fid;
import org.dan.jadalnia.app.order.pojo.MarkOrderPaid;
import org.dan.jadalnia.app.order.pojo.OrderItem;
import org.dan.jadalnia.app.order.pojo.OrderLabel;
import org.dan.jadalnia.app.user.UserSession;
import org.dan.jadalnia.app.user.UserType;
import org.dan.jadalnia.app.user.WsClientHandle;
import org.dan.jadalnia.app.ws.MessageForClient;
import org.dan.jadalnia.mock.MyRest;
import org.dan.jadalnia.test.match.PredicateStateMatcher;
import org.dan.jadalnia.test.ws.WsIntegrationTest;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.Collections.singletonList;
import static org.dan.jadalnia.app.festival.NewFestivalTest.createFestival;
import static org.dan.jadalnia.app.festival.NewFestivalTest.genAdminKey;
import static org.dan.jadalnia.app.festival.SetFestivalStateTest.setState;
import static org.dan.jadalnia.app.festival.SetMenuTest.FRYTKI;
import static org.dan.jadalnia.app.festival.SetMenuTest.setMenu;
import static org.dan.jadalnia.app.order.CustomerPutsOrderTest.putOrder;
import static org.dan.jadalnia.app.user.CustomerGetsFestivalStatusOnConnectTest.genUserKey;
import static org.dan.jadalnia.app.user.CustomerGetsFestivalStatusOnConnectTest.registerCustomer;
import static org.dan.jadalnia.app.user.CustomerGetsFestivalStatusOnConnectTest.registerUser;

public class KelnerNotifiedAboutPaidOrderTest extends WsIntegrationTest {
    public static final List<OrderItem> FRYTKI_ORDER = singletonList(
            new OrderItem(FRYTKI, 1, Collections.emptyList()));

    public static UserSession registerKasier(Fid fid, String key, MyRest myRest) {
        return registerUser(fid, key, myRest, UserType.Kasier);
    }

    public static UserSession registerKelner(Fid fid, String key, MyRest myRest) {
        return registerUser(fid, key, myRest, UserType.Kelner);
    }

    public static Boolean markAsPaid(
            MyRest myRest, OrderLabel order, UserSession session) {
        return myRest.post(OrderResource.ORDER_PAID,
                session,
                new MarkOrderPaid(order),
                Boolean.class);
    }

    @Test
    @SneakyThrows
    public void customerPutsOrder() {
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

        val wsKelnerHandler = WsClientHandle.wsClientHandle(
                kelnerSession,
                new PredicateStateMatcher<>(
                        (MessageForClient event) ->
                                event instanceof OrderPaidEvent
                                        && ((OrderPaidEvent) event).getLabel().equals(orderLabel),
                        new CompletableFuture<>()),
                new TypeReference<MessageForClient>() {
                });

        bindUserWsHandler(wsKelnerHandler);

        markAsPaid(myRest(), orderLabel, kasierSession);
        // todo customer should be also notified

        wsKelnerHandler.waitTillMatcherSatisfied();
    }
}
