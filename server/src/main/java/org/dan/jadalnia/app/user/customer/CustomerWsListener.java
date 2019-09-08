package org.dan.jadalnia.app.user.customer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.dan.jadalnia.app.festival.FestivalService;
import org.dan.jadalnia.app.user.UserInfo;
import org.dan.jadalnia.app.user.UserSession;
import org.dan.jadalnia.app.user.UserType;
import org.dan.jadalnia.app.ws.PropertyUpdated;
import org.dan.jadalnia.app.ws.WsBroadcast;
import org.dan.jadalnia.app.ws.WsHandlerConfigurator;
import org.dan.jadalnia.app.ws.WsListener;
import org.dan.jadalnia.app.ws.WsSession;
import org.dan.jadalnia.sys.error.JadEx;
import org.dan.jadalnia.util.Futures;
import org.dan.jadalnia.util.collection.AsyncCache;

import javax.inject.Inject;
import javax.inject.Named;
import javax.websocket.CloseReason;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static java.nio.charset.StandardCharsets.UTF_8;
import static javax.websocket.CloseReason.CloseCodes.VIOLATED_POLICY;
import static org.dan.jadalnia.app.auth.AuthService.SESSION;
import static org.dan.jadalnia.app.auth.ctx.UserCacheFactory.USER_SESSIONS;
import static org.dan.jadalnia.sys.error.Exceptions.rootCause;
import static org.dan.jadalnia.sys.error.JadEx.badRequest;
import static org.dan.jadalnia.sys.error.JadEx.internalError;
import static org.dan.jadalnia.util.Strings.cutLongerThan;

@Slf4j
@ServerEndpoint(
        value = "/ws/customer",
        configurator = WsHandlerConfigurator.class)
public class CustomerWsListener implements WsListener {
    public static final int WS_CLOSE_REASON_LIMIT = 120;
    @Inject
    WsBroadcast wsBroadcast;
    @Inject
    @Named(USER_SESSIONS)
    AsyncCache<UserSession, UserInfo> userSessions;
    @Inject
    ObjectMapper objectMapper;
    @Inject
    FestivalService festivalService;

    Optional<WsSession> oSession = Optional.empty();
    Optional<UserSession> oUserSession = Optional.empty();
    Optional<UserInfo> oUserInfo = Optional.empty();

    @Override
    public CompletableFuture<Void> send(byte[] message) {
        return handleException(() -> getSession().send(message));
    }

    private WsSession getSession() {
        return oSession.orElseThrow(
                () -> internalError("WS connection without [" + SESSION + "] header"));
    }

    private UserSession getUserSession() {
        return oUserSession.orElseThrow(() -> internalError("WS connection without user session"));
    }

    @OnOpen
    @SneakyThrows
    public void onConnected(Session httpSession) {
        oSession = Optional.of(WsSession.wrap(httpSession));
        handleException(() -> {
            oUserSession = Optional.of(
                    getSession().header(SESSION, UserSession::valueOf));

            return userSessions.get(getUserSession()).thenCompose(userInfo -> {
                if (userInfo.getUserType() != UserType.Customer) {
                    throw badRequest("Just Customers are expected");
                }
                log.info("Connected customer {} of festival {}",
                        getUserSession().getUid(), userInfo.getFid());

                val listeners = wsBroadcast.getListeners(userInfo.getFid());
                val earlier = listeners.getCustomerListeners()
                        .putIfAbsent(userInfo.getUid(), this);
                if (earlier != null) {
                    throw badRequest("Multiple web sockets are not allowed");
                }
                log.info("Online customers {} for {}",
                        listeners.getCustomerListeners().size(),
                        userInfo.getFid());

                oUserInfo = Optional.of(userInfo);

                return sendFestivalStatus();
            });
        });
    }

    private CompletableFuture<Void> sendFestivalStatus() {
        return oUserInfo.map(UserInfo::getFid)
                .map(fid -> festivalService.getState(fid).thenCompose(state ->
                        send(PropertyUpdated
                                .builder()
                                .name(FestivalService.FESTIVAL_STATE)
                                .newValue(state)
                                .build())))
                .orElseGet(Futures::voidF);
    }

    @SneakyThrows
    public <T> CompletableFuture<Void> send(T msgToClient) {
        log.info("Send [{}] to ws client {}",
                msgToClient, oUserInfo.map(UserInfo::getUid));
        return send(objectMapper.writeValueAsBytes(msgToClient));
    }

    @OnMessage
    public void onMessage(String message) {
        log.info("WS message [{}] from {} in festival {}", message,
                oUserInfo.map(UserInfo::getUid),
                oUserInfo.map(UserInfo::getFid));
    }

    @OnError
    public void onError(Throwable e) {
        log.error("WS for uid {} failed: {}",
                oUserInfo.map(UserInfo::getUid), e.getMessage(), e);
        oSession.ifPresent(session -> session.send(e.getMessage().getBytes(UTF_8))
                .whenComplete((r, ee) -> {
                    if (ee != null) {
                        log.error("Error message deliver problem", ee);
                    }
                    closeWs(e);
                }));
    }

    private void closeWs(Throwable e) {
        try {
            log.info("Close WS of {}", oUserInfo);
            getSession().getSession().close(
                    new CloseReason(
                            VIOLATED_POLICY,
                            formatCloseReason(e)));
            oSession = Optional.empty();
        } catch (Exception eee) {
            log.error("Failed to close WS {} with message {}",
                    oUserInfo, e.getMessage(), eee);
        }
    }

    private static String formatCloseReason(Throwable e) {
        return cutLongerThan(extractExceptionMessage(rootCause(e)),
                WS_CLOSE_REASON_LIMIT);
    }

    private static String extractExceptionMessage(Throwable e) {
        if (e instanceof JadEx) {
            return  ((JadEx) e).getClientMessage().getMessage();
        }
        return e.getMessage();
    }

    protected <T> CompletableFuture<T> handleException(
            Supplier<CompletableFuture<T>> futureFactory) {
        try {
            return futureFactory.get().exceptionally(e -> {
                onError(e);
                return null;
            });
        } catch (Throwable e) {
            onError(e);
            CompletableFuture<T> failure = new CompletableFuture<>();
            failure.completeExceptionally(e);
            return failure;
        }
    }
}
