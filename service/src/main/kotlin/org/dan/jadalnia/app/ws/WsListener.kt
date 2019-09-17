package org.dan.jadalnia.app.ws

import java.util.concurrent.CompletableFuture

interface WsListener {
    fun send(message: ByteArray): CompletableFuture<Void>
}
