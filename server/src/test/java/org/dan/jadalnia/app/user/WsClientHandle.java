package org.dan.jadalnia.app.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.dan.jadalnia.test.ws.WsHandler;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import javax.websocket.CloseReason;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singletonMap;
import static javax.websocket.CloseReason.CloseCodes.getCloseCode;
import static org.dan.jadalnia.app.auth.AuthService.SESSION;

@Slf4j
@WebSocket
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class WsClientHandle implements WsHandler {
    @Getter
    Map<String, String> headers;
    CompletableFuture<CloseReason> closeReasonF = new CompletableFuture<>();

    @NonFinal
    Optional<Session> oSession;

    public WsClientHandle(UserSession session) {
        this(singletonMap(SESSION, session.toString()));
    }

    public WsClientHandle() {
        this(Collections.emptyMap());
    }

    @SneakyThrows
    public CloseReason waitTillClosed() {
        return closeReasonF.get(110000L, TimeUnit.SECONDS);
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        oSession = Optional.of(session);
    }

    @SneakyThrows
    @OnWebSocketMessage
    public void onBinMessage(InputStream msg) {
        onMessage(IOUtils.toString(msg, UTF_8));
    }

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

    public void close() {
        oSession.ifPresent(Session::close);
    }
}
