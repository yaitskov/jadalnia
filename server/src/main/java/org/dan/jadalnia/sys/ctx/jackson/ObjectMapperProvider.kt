package org.dan.jadalnia.sys.ctx.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import org.dan.jadalnia.sys.jackson.ObjectMapperFactory
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.http.converter.json.SpringHandlerInstantiator

class ObjectMapperProvider {
    @Bean(name = [OBJECT_MAPPER])
    fun objectMapper(beanFactory: AutowireCapableBeanFactory): ObjectMapper {
        return get()
            .setHandlerInstantiator(
                SpringHandlerInstantiator(beanFactory)) as ObjectMapper
    }

    companion object {
        const val OBJECT_MAPPER = "om"

        @JvmStatic
        fun get() = ObjectMapperFactory.create()
    }
}
