package org.dan.jadalnia.app.user;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.dan.jadalnia.test.ws.WsIntegrationTest;
import org.hamcrest.Matcher;
import org.junit.Test;

import javax.websocket.CloseReason;

import static javax.websocket.CloseReason.CloseCodes.VIOLATED_POLICY;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.junit.Assert.assertThat;

@Slf4j
public class CustomerConnectsWithoutSessionTest
        extends WsIntegrationTest {
    @Test
    @SneakyThrows
    public void serverClosesConnection() {
        assertThat(bindWsHandler("/ws/customer", new WsClientHandle()).waitTillClosed(),
                expectErrnoWsCloseReason("Header [session] is missing"));
    }

    static Matcher<CloseReason> expectErrnoWsCloseReason(String expectedErrorMsg) {
        return allOf(
                hasProperty("closeCode", is(VIOLATED_POLICY)),
                hasProperty("reasonPhrase",
                        containsString(expectedErrorMsg)));
    }
}
