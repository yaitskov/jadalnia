package org.dan.jadalnia.app.user


import org.dan.jadalnia.app.ws.WsListener
import org.slf4j.LoggerFactory

import javax.websocket.OnOpen
import javax.websocket.Session
import java.nio.ByteBuffer
import java.util.Optional
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicReference

class ParticipantWsListener(
        val session: AtomicReference<Optional<Session>>
        = AtomicReference(Optional.empty()))
    : WsListener {

    companion object {
        val log = LoggerFactory.getLogger(ParticipantWsListener::class.java)
    }

    @OnOpen
    fun connect(session: Session) {
        this.session.set(Optional.of(session));
        // session.getAsyncRemote().sendText()
    }

    override fun send(message: ByteArray): CompletableFuture<Void> {
        val result = CompletableFuture<Void>()
        session.get()
                .map(Session::getAsyncRemote)
                .ifPresent({ endpoint ->
                    endpoint.sendObject(
                            ByteBuffer.wrap(message),
                            { sendResult ->
                                if (sendResult.isOK()) {
                                    result.complete(null);
                                } else {
                                    result.completeExceptionally(sendResult.getException())
                                    log.error("Failed to send message");
                                }
                            })
                })
        return result
    }
}
