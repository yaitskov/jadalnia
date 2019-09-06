package org.dan.jadalnia.test.ws;

import lombok.SneakyThrows;
import lombok.val;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.junit.After;
import org.junit.Before;

import java.net.URI;

import static org.dan.jadalnia.test.ws.EmbeddedJetty.EMBEDDED_JETTY;

public abstract class WsIntegrationTest {
    private WebSocketClient wsClient;

    @Before
    public void setUp() {
        EMBEDDED_JETTY.ensureServerRunningOn(getWsPort());
        startWsClient();
    }

    @After
    public void tearDown() {
        stopWsClient();
    }

    @SneakyThrows
    private void startWsClient() {
        wsClient = new WebSocketClient();
        wsClient.start();
    }

    @SneakyThrows
    private void stopWsClient() {
        if (wsClient != null){
            wsClient.stop();
        }
    }

    protected int getWsPort() {
        return 8084;
    }

    @SneakyThrows
    protected <T extends WsHandler> T bindWsHandler(
            String urlPath, T wsHandler) {
        val upgradeReq = new ClientUpgradeRequest();
        upgradeReq.setHeader("Accept", "application/json");

        wsClient.connect(
                wsHandler,
                new URI("ws://localhost:" + getWsPort() + urlPath),
                upgradeReq);
        return wsHandler;
    }
}
