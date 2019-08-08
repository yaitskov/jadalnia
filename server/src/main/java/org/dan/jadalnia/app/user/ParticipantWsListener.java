package org.dan.jadalnia.app.user;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.dan.jadalnia.app.ws.WsListener;

import javax.inject.Inject;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@FieldDefaults(makeFinal = true)
public class ParticipantWsListener implements WsListener {
    AtomicReference<Optional<Session>> session = new AtomicReference<>(Optional.empty());

    @OnOpen
    public void connect(Session session) {
        this.session.set(Optional.of(session));
        // session.getAsyncRemote().sendText()
    }

    public CompletableFuture<Void> send(byte[] message) {
        val result = new CompletableFuture<Void>();
        session.get()
                .map(Session::getAsyncRemote)
                .ifPresent(endpoint -> endpoint.sendObject(
                        ByteBuffer.wrap(message), sendResult -> {
                            if (sendResult.isOK()) {
                                result.complete(null);
                            } else {
                                result.completeExceptionally(sendResult.getException());
                                log.error("Failed to send message");
                            }
                        }));
        return result;
    }
}
