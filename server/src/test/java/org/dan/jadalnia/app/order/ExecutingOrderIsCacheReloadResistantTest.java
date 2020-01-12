package org.dan.jadalnia.app.order;

import lombok.val;
import org.dan.jadalnia.app.festival.FestivalResource;
import org.dan.jadalnia.app.festival.pojo.CreatedFestival;
import org.dan.jadalnia.app.festival.pojo.FestivalState;
import org.dan.jadalnia.app.order.pojo.OrderLabel;
import org.dan.jadalnia.app.order.pojo.OrderState;
import org.dan.jadalnia.app.user.UserSession;
import org.dan.jadalnia.app.user.WsClientHandle;
import org.dan.jadalnia.app.ws.MessageForClient;
import org.dan.jadalnia.mock.MyRest;
import org.dan.jadalnia.test.ws.WsIntegrationTest;
import org.easymock.internal.matchers.Or;
import org.junit.Test;

import java.util.Optional;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.dan.jadalnia.app.festival.NewFestivalTest.createFestival;
import static org.dan.jadalnia.app.festival.NewFestivalTest.genAdminKey;
import static org.dan.jadalnia.app.festival.SetFestivalStateTest.setState;
import static org.dan.jadalnia.app.festival.SetMenuTest.setMenu;
import static org.dan.jadalnia.app.order.CustomerNotifiedAboutOrderExecutingTest.tryExecOrder;
import static org.dan.jadalnia.app.order.CustomerNotifiedThatOrderIsReadyTest.markAsReady;
import static org.dan.jadalnia.app.order.CustomerPutsOrderTest.putOrder;
import static org.dan.jadalnia.app.order.EventWatchers.orderWatcher;
import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.FRYTKI_ORDER;
import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.markAsPaid;
import static org.dan.jadalnia.app.order.TripleSession.triSession;


public class ExecutingOrderIsCacheReloadResistantTest extends WsIntegrationTest {
    public static void clearFestivalCache(
            MyRest myRest, UserSession session) {
        myRest.voidPost(FestivalResource.INVALIDATE_CACHE, session, emptyMap());
    }

    @Test
    public void kelnerMarksOrderAsReadyForPickup() {
        CreatedFestival festival = createFestival(genAdminKey(), myRest());
        TripleSession sessions = triSession(festival.getFid(), myRest());

        setState(myRest(), festival.getSession(), FestivalState.Open);
        setMenu(myRest(), festival.getSession());

        OrderLabel orderLabel = putOrder(myRest(), sessions.customer, FRYTKI_ORDER);

        markAsPaid(myRest(), orderLabel, sessions.cashier);

        WsClientHandle<MessageForClient> wsCustomerHandler = orderWatcher(sessions.customer, orderLabel, OrderState.Ready);

        bindCustomerWsHandler(wsCustomerHandler);

        assertThat(tryExecOrder(myRest(), sessions.kelner)).isEqualTo(Optional.of(orderLabel));

        clearFestivalCache(myRest(), festival.getSession());

        markAsReady(myRest(), orderLabel, sessions.kelner);

        wsCustomerHandler.waitTillMatcherSatisfied();
    }
}
