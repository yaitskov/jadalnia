package org.dan.jadalnia.app.user;


import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.dan.jadalnia.app.festival.pojo.Fid;
import org.dan.jadalnia.app.ws.PropertyUpdated;
import org.dan.jadalnia.mock.MyRest;
import org.dan.jadalnia.test.ws.WsIntegrationTest;
import org.junit.Test;

import java.util.UUID;

import static org.dan.jadalnia.app.festival.NewFestivalTest.createFestival;
import static org.dan.jadalnia.app.festival.NewFestivalTest.genAdminKey;
import static org.dan.jadalnia.app.festival.pojo.FestivalState.Announce;
import static org.dan.jadalnia.app.user.WsClientHandle.wsClientHandle;
import static org.dan.jadalnia.test.match.PredicateStateMatcher.passIf;

@Slf4j
public class CustomerGetsFestivalStatusOnConnectTest
        extends WsIntegrationTest {
    public static UserSession registerCustomer(Fid fid, String key, MyRest myRest) {
        return registerUser(fid, key, myRest, UserType.Customer);
    }

    public static UserSession registerUser(
            Fid fid, String key, MyRest myRest, UserType userType) {
        return myRest.anonymousPost(UserResource.REGISTER,
                new UserRegRequest(fid, "user" + key, key, userType),
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

        val wsHandler = wsClientHandle(customerSession,
                passIf((PropertyUpdated event)
                        -> event.getNewValue().equals(Announce.name())),
                new TypeReference<PropertyUpdated>() {});

        bindCustomerWsHandler(wsHandler);

        wsHandler.waitTillMatcherSatisfied();
    }
}
