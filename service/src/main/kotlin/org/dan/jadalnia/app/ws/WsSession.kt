package org.dan.jadalnia.app.ws

import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest

import javax.websocket.Session
import java.io.IOException
import java.nio.ByteBuffer

import java.util.concurrent.CompletableFuture

import org.dan.jadalnia.sys.error.JadEx.Companion.badRequest
import java.util.Optional.ofNullable
import org.dan.jadalnia.org.dan.jadalnia.app.auth.AuthService.SESSION
import org.eclipse.jetty.websocket.common.WebSocketSession

class WsSession(
        val session: Session,
        val request: ServletUpgradeRequest) {

    companion object {
        @JvmStatic
        fun wrap(session: Session): WsSession {
            val jsrSession = session as WebSocketSession
            return WsSession(
                    session, jsrSession.upgradeRequest as ServletUpgradeRequest)
        }
    }

    fun header(name: String)
        = ofNullable(request.getHeader(name))

    fun <T> header(name: String, factory: (String) -> T): T {
        return header(name)
                .map(factory)
                .orElseThrow({ badRequest("Header [$name] is missing") })
    }

    fun send(message: ByteArray): CompletableFuture<Void> {
        val sent = CompletableFuture<Void>()
        session.getAsyncRemote()
                .sendBinary(
                        ByteBuffer.wrap(message),
                        { sendResult ->
                            if (sendResult.isOK()) {
                                sent.complete(null)
                            } else {
                                sent.completeExceptionally(
                                        IOException(
                                                "Failed to send message to "
                                                        + request.getHeader(SESSION),
                                                sendResult.getException()));
                            }
                        })
        return sent
    }
}
