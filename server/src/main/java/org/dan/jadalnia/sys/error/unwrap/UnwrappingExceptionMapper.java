package org.dan.jadalnia.sys.error.unwrap;

import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.spi.ExceptionMappers;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

@Slf4j
public class UnwrappingExceptionMapper<T extends Throwable>
        implements ExceptionMapper<T> {
    @Inject
    private Provider<ExceptionMappers> mappersProvider;

    @Override
    public Response toResponse(T exception) {
        log.error(
                "Wrapper exception {}",
                exception.getClass().getCanonicalName());
        return mappersProvider.get()
                .findMapping(exception.getCause())
                .toResponse(exception.getCause());
    }
}
