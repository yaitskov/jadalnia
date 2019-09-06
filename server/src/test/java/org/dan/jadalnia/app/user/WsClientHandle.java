package org.dan.jadalnia.app.user;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.dan.jadalnia.test.ws.WsHandler;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import javax.websocket.CloseReason;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static javax.websocket.CloseReason.CloseCodes.getCloseCode;

@Slf4j
@WebSocket
public class WsClientHandle implements WsHandler {
    private Optional<Session> oSession;
    public final CompletableFuture<CloseReason> closeReasonF
            = new CompletableFuture<>();

    @SneakyThrows
    public CloseReason waitTillClosed() {
        return closeReasonF.get(11L, TimeUnit.SECONDS);
    }

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
