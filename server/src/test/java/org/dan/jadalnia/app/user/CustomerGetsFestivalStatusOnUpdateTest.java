package org.dan.jadalnia.app.user;


import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.dan.jadalnia.app.ws.PropertyUpdated;
import org.dan.jadalnia.test.match.PredicateStateMatcher;
import org.dan.jadalnia.test.ws.WsIntegrationTest;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

import static org.dan.jadalnia.app.festival.NewFestivalTest.createFestival;
import static org.dan.jadalnia.app.festival.NewFestivalTest.genAdminKey;
import static org.dan.jadalnia.app.festival.SetFestivalStateTest.setState;
import static org.dan.jadalnia.app.festival.pojo.FestivalState.Open;
import static org.dan.jadalnia.app.user.CustomerGetsFestivalStatusOnConnectTest.genUserKey;
import static org.dan.jadalnia.app.user.CustomerGetsFestivalStatusOnConnectTest.registerCustomer;

@Slf4j
public class CustomerGetsFestivalStatusOnUpdateTest
        extends WsIntegrationTest {
    @Test
    @SneakyThrows
    public void serverSendsFestivalStatus() {
        val key = genAdminKey();
        val festival = createFestival(key, myRest());
        val customerSession = registerCustomer(
                festival.getFid(), genUserKey(), myRest());

        val wsHandler = WsClientHandle.wsClientHandle(
                customerSession,
                new PredicateStateMatcher<>(
                        event -> event.getNewValue().equals(Open.name()),
                        new CompletableFuture<>()),
                new TypeReference<PropertyUpdated<String>>() {
                        });

        bindCustomerWsHandler(wsHandler);

        setState(myRest(), festival.getSession(), Open);

        wsHandler.waitTillMatcherSatisfied();
    }
}
