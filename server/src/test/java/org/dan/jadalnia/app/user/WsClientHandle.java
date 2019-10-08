package org.dan.jadalnia.app.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.dan.jadalnia.app.ws.MessageForClient;
import org.dan.jadalnia.org.dan.jadalnia.app.auth.AuthService;
import org.dan.jadalnia.sys.ctx.jackson.ObjectMapperProvider;
import org.dan.jadalnia.test.match.PredicateStateMatcher;
import org.dan.jadalnia.test.match.StateMatcher;
import org.dan.jadalnia.test.ws.WsHandler;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.jetbrains.annotations.NotNull;

import javax.websocket.CloseReason;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static java.util.Collections.singletonMap;
import static javax.websocket.CloseReason.CloseCodes.getCloseCode;


@Slf4j
@WebSocket
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class WsClientHandle<T> implements WsHandler {
    TypeReference<T> baseMsgClass;
    ObjectMapper objectMapper;
    @Getter
    Map<String, String> headers;
    CompletableFuture<CloseReason> closeReasonF = new CompletableFuture<>();
    StateMatcher<T> inMessageMatcher;

    @NonFinal
    Optional<Session> oSession;

    public static <T extends MessageForClient> WsClientHandle<T> wsClientHandle(
            UserSession session,
            StateMatcher<T> inMessageMatcher, TypeReference<T> inMessageType) {
        return new WsClientHandle<>(
                inMessageType,
                ObjectMapperProvider.Companion.get(),
                singletonMap(AuthService.SESSION, session.toString()),
                inMessageMatcher);
    }

    public static WsClientHandle<MessageForClient> anonymousHandler() {
        return new WsClientHandle<>(
                new TypeReference<MessageForClient>() {},
                ObjectMapperProvider.Companion.get(),
                Collections.emptyMap(),
                voidPredicate());
    }

    @NotNull
    public static <T extends MessageForClient> StateMatcher<T> voidPredicate() {
        return new PredicateStateMatcher<>(o -> false, new CompletableFuture<>());
    }

    @SneakyThrows
    public CloseReason waitTillClosed() {
        return closeReasonF.get(11L, TimeUnit.SECONDS);
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        oSession = Optional.of(session);
    }

    @SneakyThrows
    @OnWebSocketMessage
    public void onBinMessage(InputStream msg) {
        onMessage(objectMapper.readValue(msg, baseMsgClass));
    }

    public void onMessage(T msg) {
        log.info("Client ws has got message [{}]", msg);
        inMessageMatcher.was(msg);
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

    public T waitTillMatcherSatisfied() {
        return waitTillMatcherSatisfied("default");
    }

    @SneakyThrows
    public T waitTillMatcherSatisfied(String condition) {
        try {
            return inMessageMatcher.satisfied(condition).get(6L, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            throw inMessageMatcher.report(condition);
        }
    }
}
