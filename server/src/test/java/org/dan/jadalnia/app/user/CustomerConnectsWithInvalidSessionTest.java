package org.dan.jadalnia.app.user;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.dan.jadalnia.app.ws.PropertyUpdated;
import org.dan.jadalnia.test.ws.WsIntegrationTest;
import org.junit.Test;

import static org.dan.jadalnia.app.user.CustomerConnectsWithoutSessionTest.expectErrnoWsCloseReason;
import static org.dan.jadalnia.app.user.WsClientHandle.voidPredicate;
import static org.dan.jadalnia.app.user.WsClientHandle.wsClientHandle;
import static org.junit.Assert.assertThat;

@Slf4j
public class CustomerConnectsWithInvalidSessionTest
        extends WsIntegrationTest {
    @Test
    @SneakyThrows
    public void serverClosesConnection() {
        assertThat(
                bindWsHandler(
                        "/ws/customer",
                        wsClientHandle(
                                new UserSession(Uid.of(10000000), "abc"),
                                voidPredicate(),
                                new TypeReference<PropertyUpdated>() {}),
                        getWsClient())
                        .waitTillClosed(),
                expectErrnoWsCloseReason("session is not valid"));
    }
}
