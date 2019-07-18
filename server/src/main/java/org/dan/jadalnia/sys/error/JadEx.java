package org.dan.jadalnia.sys.error;

import static org.eclipse.jetty.http.HttpStatus.BAD_REQUEST_400;
import static org.eclipse.jetty.http.HttpStatus.FORBIDDEN_403;
import static org.eclipse.jetty.http.HttpStatus.INTERNAL_SERVER_ERROR_500;
import static org.eclipse.jetty.http.HttpStatus.NOT_FOUND_404;
import static org.eclipse.jetty.http.HttpStatus.UNAUTHORIZED_401;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Getter
public class JadEx extends RuntimeException {
    private final int status;
    private final Error clientMessage;


    public JadEx(int status, Error clientMessage, Throwable cause) {
        super(clientMessage.toString(), cause);
        this.status = status;
        this.clientMessage = clientMessage;
    }

    public static JadEx notFound(String clientMessage) {
        return new JadEx(NOT_FOUND_404, new Error(clientMessage), null);
    }

    public static JadEx notFound(String template, String param, Object value) {
        return new JadEx(NOT_FOUND_404,
                new TemplateError(template, ImmutableMap.of(param, value)), null);
    }

    public static JadEx notAuthorized(String msg) {
        return new JadEx(UNAUTHORIZED_401, new Error(msg), null);
    }

    public static JadEx forbidden(String template, String param, Object value) {
        return new JadEx(FORBIDDEN_403,
                new TemplateError(template, ImmutableMap.of(param, value)),
                null);
    }

    public static JadEx forbidden(String clientMessage) {
        return new JadEx(FORBIDDEN_403, new Error(clientMessage), null);
    }

    public static JadEx badRequest(String clientMessage) {
        return badRequest(
                new Error(UUID.randomUUID().toString(),
                clientMessage));
    }

    public static JadEx badRequest(String clientMessage, Exception e) {
        return badRequest(
                new Error(UUID.randomUUID().toString(),
                        clientMessage), e);
    }

    public static JadEx badRequest(Error error) {
        return badRequest(error, null);
    }

    public static JadEx badRequest(String messageTemplate, Map<String, Object> params) {
        return badRequest(new TemplateError(messageTemplate, params));
    }

    public static JadEx badRequest(String messageTemplate, String param, Object value) {
        return badRequest(messageTemplate, ImmutableMap.of(param, value));
    }

    public static JadEx badRequest(Error clientMessage, Exception e) {
        return new JadEx(BAD_REQUEST_400, clientMessage, e);
    }

    public static JadEx internalError(String clientMessage) {
        return internalError(clientMessage, Collections.emptyMap());
    }

    public static JadEx internalError(String messageTemplate, Map<String, Object> params) {
        return internalError(new TemplateError(messageTemplate, params));
    }

    public static JadEx internalError(String messageTemplate, String param, Object value) {
        return internalError(messageTemplate, ImmutableMap.of(param, value));
    }

    public static JadEx internalError(Error error) {
        return new JadEx(INTERNAL_SERVER_ERROR_500, error, null);
    }

    public static JadEx internalError(String clientMessage, Exception e) {
        return new JadEx(INTERNAL_SERVER_ERROR_500,
                new TemplateError(clientMessage, Collections.emptyMap()), e);
    }
}
