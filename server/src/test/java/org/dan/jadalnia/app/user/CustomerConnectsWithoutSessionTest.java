package org.dan.jadalnia.app.user;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.dan.jadalnia.app.user.customer.CustomerWsListener;
import org.dan.jadalnia.sys.ctx.TestCtx;
import org.dan.jadalnia.test.AbstractSpringJerseyTest;
import org.dan.jadalnia.test.JerseySpringTest;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.test.context.ContextConfiguration;

import javax.websocket.CloseReason;

import java.net.URI;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.TimeUnit.SECONDS;
import static javax.websocket.CloseReason.CloseCodes.VIOLATED_POLICY;
import static javax.websocket.CloseReason.CloseCodes.getCloseCode;
import static org.dan.jadalnia.app.festival.NewFestivalTest.createFestival;
import static org.dan.jadalnia.app.festival.NewFestivalTest.genAdminKey;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@Slf4j
@Category(JerseySpringTest.class)
@ContextConfiguration(classes = TestCtx.class)
public class CustomerConnectsWithoutSessionTest extends AbstractSpringJerseyTest {
    WebSocketClient wsClient;

//    @Before
//    @SneakyThrows
//    public void setUp() {
//        wsClient = new WebSocketClient();
//        wsClient.start();
//    }
//
//    @After
//    @SneakyThrows
//    public void tearDown() {
//        if (wsClient != null) {
//            wsClient.stop();
//        }
//    }

    @Test
    @SneakyThrows
    public void closeWs() {
        val key = genAdminKey();
        val result = createFestival(key, myRest());

        wsClient = new WebSocketClient();
        wsClient.start();

        val upgradeReq = new ClientUpgradeRequest();
        upgradeReq.setHeader("Accept", "application/json");
//        upgradeReq.setHeader(AuthService.SESSION,
//                UserSession.builder().uid(Uid.of(123)).key("key").build().toString());
        WsClientHandle wsHandler = new WsClientHandle();
        wsClient.connect(
                wsHandler,
                new URI(getBaseUri().toString().replace("http", "ws") + "ws/customer"),
                upgradeReq);
//
        assertThat(wsHandler.closeReasonF.get(11L, SECONDS),
                allOf(
                        hasProperty("closeCode", is(VIOLATED_POLICY)),
                        hasProperty("reasonPhrase",
                                containsString("WS connection without SESSION"))));
    }

    @WebSocket
    class WsClientHandle {
        private Optional<Session> oSession;
        private final CompletableFuture<CloseReason> closeReasonF
                = new CompletableFuture<>();

        @OnWebSocketConnect
        public void onConnect(Session session) {
            oSession = Optional.of(session);
        }

        @OnWebSocketMessage
        public void onMessage(String msg) {
            log.info("Client ws has got message [{}]", msg);
        }

        @OnWebSocketError
        public void onError(Throwable e) {
            log.info("Client ws has got error", e);
        }

        @OnWebSocketClose
        public void onClose(int code, String reason) {
            log.info("Client ws is closed {} {}", code, reason);
            closeReasonF.complete(new CloseReason(getCloseCode(code), reason));
        }
    }
}
