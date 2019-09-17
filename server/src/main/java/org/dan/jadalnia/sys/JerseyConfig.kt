package org.dan.jadalnia.sys

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider
import org.dan.jadalnia.app.festival.FestivalResource
import org.dan.jadalnia.app.order.OrderResource
import org.dan.jadalnia.app.user.UserResource
import org.dan.jadalnia.sys.ctx.jackson.ObjectMapperContextResolver
import org.dan.jadalnia.sys.error.DefaultExceptionMapper
import org.dan.jadalnia.sys.error.InvalidTypeIdExceptionMapper
import org.dan.jadalnia.sys.error.JadExMapper
import org.dan.jadalnia.sys.error.JerseyExceptionMapper
import org.dan.jadalnia.sys.error.JerseyValidationExceptionMapper
import org.dan.jadalnia.sys.error.JooqExceptionMapper
import org.dan.jadalnia.sys.error.JsonMappingExceptionMapper
import org.dan.jadalnia.sys.error.UncheckedExecutionExceptionMapper
import org.dan.jadalnia.sys.error.UndeclaredThrowableExecutionExceptionMapper
import org.dan.jadalnia.sys.error.UnrecognizedPropertyExceptionMapper
import org.glassfish.jersey.filter.LoggingFilter
import org.glassfish.jersey.server.ResourceConfig

import javax.ws.rs.ApplicationPath

import java.util.Arrays.asList
import java.util.stream.Collectors.toList
import org.glassfish.jersey.server.ServerProperties.BV_SEND_ERROR_IN_RESPONSE

@ApplicationPath("/")
class JerseyConfig : ResourceConfig() {
    init {
        property(BV_SEND_ERROR_IN_RESPONSE, true)
        register(LoggingFilter())
        register(ObjectMapperContextResolver::class.java)
        register(JacksonJaxbJsonProvider::class.java)
        register(JadExMapper())
        register(JerseyExceptionMapper()) // just class get exception
        register(JerseyValidationExceptionMapper())
        register(DefaultExceptionMapper())
        register(UnrecognizedPropertyExceptionMapper::class.java)
        register(JooqExceptionMapper::class.java)
        register(JsonMappingExceptionMapper::class.java)
        register(InvalidTypeIdExceptionMapper::class.java)
        register(UncheckedExecutionExceptionMapper::class.java)
        register(UndeclaredThrowableExecutionExceptionMapper::class.java)
        packages(false,
                *asList(UserResource::class.java,
                        FestivalResource::class.java, OrderResource::class.java)
                        .stream()
                        .map({ cls -> cls.`package`.name })
                        .collect(toList())
                        .toTypedArray())
    }
}
