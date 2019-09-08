package org.dan.jadalnia.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.io.IOUtils;
import org.dan.jadalnia.app.user.UserSession;
import org.dan.jadalnia.sys.ctx.jackson.ObjectMapperProvider;
import org.dan.jadalnia.sys.error.Error;
import org.dan.jadalnia.sys.error.JadEx;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singletonMap;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.dan.jadalnia.app.auth.AuthService.SESSION;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

@RequiredArgsConstructor
public class MyRest {
    private static final ObjectMapper om = ObjectMapperProvider.get();
    private final Client client;
    private final URI baseUri;

    public WebTarget request() {
        return client.target(baseUri);
    }

    public <T, R> R anonymousPost(String path, T entity, Class<R> respClass) {
        return post(path, Optional.empty(), entity, respClass);
    }

    public <T, R> R post(String path, UserSession session, T entity, Class<R> respClass) {
        return post(path, Optional.of(session), entity, respClass);
    }

    @SneakyThrows
    public <T, R> R post(String path, Optional<UserSession> session, T entity, Class<R> respClass) {
        final Response response = post(path, session, entity);
        switch (response.getStatus()) {
            case 200:
            case 201:
            case 204:
                return response.readEntity(respClass);
            case 400:
                throw new JadEx(400, response.readEntity(Error.class), null);
            default:
                throw new JadEx(
                        response.getStatus(),
                        new Error("post req ["
                                + path + "] with ["
                                + om.writeValueAsString(entity)
                                + "] responded [" + response.getStatus() + "] ["
                                + response.getStatusInfo().getReasonPhrase() + "] ["
                                + IOUtils.toString((InputStream) response.getEntity() , UTF_8)
                                + "]"
                        ),
                        null);
        }
    }

    public <T> Invocation.Builder postBuilder(
            String path, Map<String, String> headers) {
        val request = request().path(path).request(APPLICATION_JSON);
        headers.forEach(request::header);
        return request;
    }

    public <T> Response post(
            String path, Optional<UserSession> session, T entity) {
        return post(path,
                session.map(UserSession::toString)
                        .map(sessionKey -> singletonMap(SESSION, sessionKey))
                        .orElseGet(Collections::emptyMap),
                entity);
    }

    public <T> Response post(String path, Map<String, String> headers, T entity) {
        return postBuilder(path, headers)
                .post(Entity.entity(entity, APPLICATION_JSON));
    }

    public <T> T get(String path, SessionAware session, GenericType<T> gt) {
        return request().path(path).request(APPLICATION_JSON)
                .header(SESSION, session.getSession())
                .get(gt);
    }

    public <T> T get(String path, SessionAware session, Class<T> resultClass) {
        return request().path(path).request(APPLICATION_JSON)
                .header(SESSION, session.getSession())
                .get(resultClass);
    }

    public <T> T get(String path, Class<T> c) {
        return request().path(path).request(APPLICATION_JSON).get(c);
    }

    public <T> T get(String path, GenericType<T> c) {
        return request().path(path).request(APPLICATION_JSON).get(c);
    }
}
