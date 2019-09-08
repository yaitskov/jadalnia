package org.dan.jadalnia.app.user;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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
                                UserSession
                                        .builder()
                                        .key("abc")
                                        .uid(Uid.of(10000000))
                                        .build(),
                                voidPredicate()))
                        .waitTillClosed(),
                expectErrnoWsCloseReason("session is not valid"));
    }
}
