package org.dan.jadalnia.app.user.customer;

import org.dan.jadalnia.app.ws.WsListener;

import java.util.concurrent.CompletableFuture;

public class CustomerWsListener implements WsListener {
    @Override
    public CompletableFuture<Void> send(byte[] message) {
        return null;
    }
}
