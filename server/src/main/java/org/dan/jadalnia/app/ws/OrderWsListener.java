package org.dan.jadalnia.app.ws;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@Slf4j
@ServerEndpoint(
        value = "/ws/order",
        configurator = WsHandlerConfigurator.class)
public class OrderWsListener {
    @Inject
    private ObjectMapper objectMapper;


    @OnOpen
    public void connect(Session session) {
        session.getAsyncRemote().sendText()
    }

    @OnMessage
    public void message(String msg) {

    }

    @OnClose
    public void close() {

    }

    @OnError
    public void onError(Throwable e) {

    }
}
