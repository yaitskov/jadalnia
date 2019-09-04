package org.dan.jadalnia.app.ws;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.eclipse.jetty.websocket.jsr356.JsrSession;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;

import javax.websocket.Session;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static java.util.Optional.ofNullable;
import static org.dan.jadalnia.app.auth.AuthService.SESSION;
import static org.dan.jadalnia.sys.error.JadEx.badRequest;

@Getter
@FieldDefaults(makeFinal = true)
@RequiredArgsConstructor
public class WsSession {
    Session session;
    ServletUpgradeRequest request;

    public static WsSession wrap(Session session) {
        val jsrSession = (JsrSession) session;
        return new WsSession(session,
                (ServletUpgradeRequest) jsrSession.getUpgradeRequest());
    }

    public Optional<String> header(String name) {
        return ofNullable(request.getHeader(name));
    }

    public <T> T header(String name, Function<String, T> factory) {
        return header(name)
                .map(factory)
                .orElseThrow(() -> badRequest("Header [" + name + "] is missing"));
    }

    public CompletableFuture<Void> send(byte[] message) {
        CompletableFuture<Void> sent = new CompletableFuture<>();
        session.getAsyncRemote()
                .sendBinary(ByteBuffer.wrap(message), (sendResult) -> {
                    if (sendResult.isOK()) {
                        sent.complete(null);
                    } else {
                        sent.completeExceptionally(new IOException(
                                "Failed to send message to " + request.getHeader(SESSION),
                                sendResult.getException()));
                    }
                });
        return sent;
    }
}
