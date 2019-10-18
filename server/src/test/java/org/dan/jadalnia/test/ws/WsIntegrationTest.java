package org.dan.jadalnia.test.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.dan.jadalnia.mock.MyRest;
import org.dan.jadalnia.sys.ctx.jackson.ObjectMapperProvider;
import org.dan.jadalnia.sys.jetty.EmbeddedJetty;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.junit.After;
import org.junit.Before;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.ext.ContextResolver;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import static org.springframework.util.SocketUtils.findAvailableTcpPort;

@Slf4j
public abstract class WsIntegrationTest {
    private WebSocketClient wsClient;
    private Client httpClient;

    private void initHttpClient() {
        httpClient = ClientBuilder.newBuilder()
                .register(new ContextResolver<ObjectMapper>() {
                    public ObjectMapper getContext(Class<?> type) {
                        return ObjectMapperProvider.Companion.get();
                    }
                })
                .register(JacksonFeature.class)
                .build();
    }

    @Before
    public void setUp() {
        EmbeddedJetty.INSTANCE.ensureServerRunningOn(getWsPort());
        startWsClient();
        initHttpClient();
    }

    @After
    public void tearDown() {
        stopWsClient();
        httpClient.close();
    }

    @SneakyThrows
    private void startWsClient() {
        if (wsClient != null) {
            return;
        }
        wsClient = new WebSocketClient();
        wsClient.start();
    }

    @SneakyThrows
    private void stopWsClient() {
        if (wsClient != null){
            wsClient.stop();
        }
    }

    private static int ALLOCATED_PORT;

    private static int getWsPort() {
        if (ALLOCATED_PORT == 0) {
            ALLOCATED_PORT = findAvailableTcpPort();
            log.info("Use WS port {}", ALLOCATED_PORT);
        }
        return ALLOCATED_PORT;
    }

    @SneakyThrows
    protected MyRest myRest() {
        return new MyRest(httpClient, new URI(baseHttpUrl()));
    }

    protected String baseHttpUrl() {
        return "http://localhost:" + getWsPort();
    }

    @SneakyThrows
    protected <T extends WsHandler> T bindWsHandler(
            String urlPath, T wsHandler) {
        val upgradeReq = new ClientUpgradeRequest();

        wsHandler.getHeaders().forEach(upgradeReq::setHeader);
        upgradeReq.setHeader("Accept", "application/json");

        wsClient.connect(
                wsHandler,
                new URI("ws://localhost:" + getWsPort() + urlPath),
                upgradeReq).get(5L, TimeUnit.SECONDS);
        return wsHandler;
    }

    protected <T extends WsHandler> T bindCustomerWsHandler(T wsHandler) {
        return bindWsHandler("/ws/customer", wsHandler);
    }

    protected <T extends WsHandler> T bindUserWsHandler(T wsHandler) {
        return bindWsHandler("/ws/user", wsHandler);
    }
}
