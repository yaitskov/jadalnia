package org.dan.jadalnia.app.ws;

import java.util.concurrent.CompletableFuture;

public interface WsListener {
    CompletableFuture<Void> send(byte[] message);
}
