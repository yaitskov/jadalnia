package org.dan.jadalnia.app.user;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.dan.jadalnia.app.festival.pojo.Fid;
import org.dan.jadalnia.app.ws.PropertyUpdated;
import org.dan.jadalnia.mock.MyRest;
import org.dan.jadalnia.test.match.PredicateStateMatcher;
import org.dan.jadalnia.test.ws.WsIntegrationTest;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.dan.jadalnia.app.festival.NewFestivalTest.createFestival;
import static org.dan.jadalnia.app.festival.NewFestivalTest.genAdminKey;
import static org.dan.jadalnia.app.festival.SetFestivalStateTest.setState;
import static org.dan.jadalnia.app.festival.pojo.FestivalState.Open;

@Slf4j
public class CustomerGetsFestivalStatusOnUpdateTest
        extends WsIntegrationTest {
    public static UserSession registerCustomer(Fid fid, String key, MyRest myRest) {
        return registerUser(fid, key, myRest, UserType.Customer);
    }

    public static UserSession registerUser(
            Fid fid, String key, MyRest myRest, UserType userType) {
        return myRest.anonymousPost(UserResource.REGISTER,
                UserRegRequest
                        .builder()
                        .name("user" + key)
                        .festivalId(fid)
                        .session(key)
                        .userType(userType)
                        .build(),
                UserSession.class);
    }

    public static String genUserKey() {
        return UUID.randomUUID().toString();
    }

    @Test
    @SneakyThrows
    public void serverSendsFestivalStatus() {
        val key = genAdminKey();
        val festival = createFestival(key, myRest());
        val customerSession = registerCustomer(
                festival.getFid(), genUserKey(), myRest());

        val wsHandler = WsClientHandle.wsClientHandle(
                customerSession,
                new PredicateStateMatcher<PropertyUpdated<String>>(
                        event -> event.getNewValue().equals(Open.name()),
                        new CompletableFuture<>()));

        bindCustomerWsHandler(wsHandler);

        setState(myRest(), festival.getSession(), Open);

        wsHandler.waitTillMatcherSatisfied();
    }
}
