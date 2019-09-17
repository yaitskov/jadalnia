package org.dan.jadalnia.sys.ctx.jackson

import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY
import com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES
import com.fasterxml.jackson.databind.MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS
import com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
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
        fun get(): ObjectMapper {
            return ObjectMapper()
                    .setSerializationInclusion(NON_EMPTY)
                    .enable(ALLOW_UNQUOTED_FIELD_NAMES)
                    .enable(ALLOW_FINAL_FIELDS_AS_MUTATORS)
                    .disable(WRITE_DATES_AS_TIMESTAMPS)
                    .registerModule(Jdk8Module())
                    .registerModule(KotlinModule())
                    .registerModule(JavaTimeModule())
        }
    }
}
