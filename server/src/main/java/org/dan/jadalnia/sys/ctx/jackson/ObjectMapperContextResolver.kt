package org.dan.jadalnia.sys.ctx.jackson


import com.fasterxml.jackson.databind.ObjectMapper
import javax.ws.rs.ext.ContextResolver

class ObjectMapperContextResolver : ContextResolver<ObjectMapper> {
    companion object {
        var objectMapper = ObjectMapperProvider.get()
    }

    override fun getContext(type: Class<*>): ObjectMapper? {
        return objectMapper
    }
}
