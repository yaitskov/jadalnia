package org.dan.jadalnia.sys.error;

import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.spi.ExceptionMappers;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.concurrent.CompletionException;

@Slf4j
public class CompletionExceptionMapper
        implements ExceptionMapper<CompletionException> {
    @Inject
    private Provider<ExceptionMappers> mappersProvider;

    @Override
    public Response toResponse(CompletionException exception) {
        log.error("Wrapper exception CompletionException");
        return mappersProvider.get()
                .findMapping(exception.getCause())
                .toResponse(exception.getCause());
    }
}
