package org.dan.jadalnia.app.user;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.dan.jadalnia.app.festival.pojo.Fid;
import org.dan.jadalnia.app.ws.PropertyUpdated;
import org.dan.jadalnia.mock.MyRest;
import org.dan.jadalnia.sys.ctx.jackson.ObjectMapperProvider;
import org.dan.jadalnia.test.ws.WsIntegrationTest;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.ext.ContextResolver;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.dan.jadalnia.app.festival.FestivalService.FESTIVAL_STATE;
import static org.dan.jadalnia.app.festival.NewFestivalTest.createFestival;
import static org.dan.jadalnia.app.festival.NewFestivalTest.genAdminKey;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.junit.Assert.assertThat;

@Slf4j
public class CustomerGetsFestivalStatusOnConnectTest
        extends WsIntegrationTest {
    Client client;

    @Before
    public void init() {
        client = ClientBuilder.newBuilder()
                .register(new ContextResolver<ObjectMapper>() {
                    public ObjectMapper getContext(Class<?> type) {
                        return ObjectMapperProvider.get();
                    }
                })
                .register(JacksonFeature.class)
                .build();
    }

    static ObjectMapper objectMapper = ObjectMapperProvider.get();

    public static UserSession registerCustomer(Fid fid, String key, MyRest myRest) {
        return registerUser(fid, key, myRest, UserType.Customer);
    }

    public static UserSession registerUser(
            Fid fid, String key, MyRest myRest, UserType userType) {
        return myRest.anonymousPost(UserResource.REGISTER,
                UserRegRequest
                        .builder()
                        .name("user" + key)
                        .festivalId(fid)
                        .session(key)
                        .userType(userType)
                        .build(),
                UserSession.class);
    }

    public static String genUserKey() {
        return UUID.randomUUID().toString();
    }

    @Test
    @SneakyThrows
    public void serverSendsFestivalStatus() {
        val key = genAdminKey();
        val festival = createFestival(key, myRest());
        val customerSession = registerCustomer(
                festival.getFid(), genUserKey(), myRest());

        val firstMessage = new CompletableFuture<PropertyUpdated<String>>();
        val wsHandler = new MyWsClientHandle(customerSession, firstMessage);

        bindWsHandler("/ws/customer", wsHandler);

        assertThat(firstMessage.get(11L, TimeUnit.SECONDS),
                allOf(hasProperty("name", is(FESTIVAL_STATE)),
                        hasProperty("newValue", is("Announce"))));
    }

    @SneakyThrows
    private MyRest myRest() {
        return new MyRest(client, new URI(baseHttpUrl()));
    }

    @WebSocket
    public static class MyWsClientHandle extends WsClientHandle {
        private final CompletableFuture<PropertyUpdated<String>> firstMessage;

        public MyWsClientHandle(UserSession session, CompletableFuture<PropertyUpdated<String>> firstMessage) {
            super(session);
            this.firstMessage = firstMessage;
        }

        @Override
        @SneakyThrows
        public void onMessage(String msg) {
            firstMessage.complete(
                    objectMapper.readValue(
                            msg,
                            new TypeReference<PropertyUpdated<String>>() {}));
        }
    }
}
