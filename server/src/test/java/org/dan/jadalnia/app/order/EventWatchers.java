package org.dan.jadalnia.app.order;

import com.fasterxml.jackson.core.type.TypeReference;
import org.dan.jadalnia.app.order.pojo.OrderLabel;
import org.dan.jadalnia.app.order.pojo.OrderState;
import org.dan.jadalnia.app.user.UserSession;
import org.dan.jadalnia.app.user.WsClientHandle;
import org.dan.jadalnia.app.ws.MessageForClient;
import org.dan.jadalnia.test.match.PredicateStateMatcher;

import java.util.concurrent.CompletableFuture;

public class EventWatchers {
    public static WsClientHandle<MessageForClient> orderWatcher(
            UserSession session,
            OrderLabel orderLabel,
            OrderState expectedEvent) {
        return WsClientHandle.wsClientHandle(
                session,
                new PredicateStateMatcher<>(
                        (MessageForClient event) ->
                                event instanceof OrderStateEvent
                                        && ((OrderStateEvent) event)
                                        .getLabel().equals(orderLabel)
                                        && ((OrderStateEvent) event)
                                        .getState() == expectedEvent,
                        new CompletableFuture<>()),
                new TypeReference<MessageForClient>() {
                });
    }
}
