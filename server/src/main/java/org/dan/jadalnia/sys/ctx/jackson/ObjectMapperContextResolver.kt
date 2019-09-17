package org.dan.jadalnia.sys.ctx.jackson


import org.dan.jadalnia.sys.ctx.jackson.ObjectMapperProvider.Companion.OBJECT_MAPPER

import com.fasterxml.jackson.databind.ObjectMapper

import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.ext.ContextResolver

class ObjectMapperContextResolver : ContextResolver<ObjectMapper> {
    @Inject
    @Named(OBJECT_MAPPER)
    private var objectMapper: ObjectMapper? = null

    override fun getContext(type: Class<*>): ObjectMapper? {
        return objectMapper
    }
}
