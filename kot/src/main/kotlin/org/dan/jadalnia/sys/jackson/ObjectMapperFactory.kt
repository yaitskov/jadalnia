package org.dan.jadalnia.sys.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY
import com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES
import com.fasterxml.jackson.databind.MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS
import com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule


object ObjectMapperFactory {
  val m = ObjectMapper()
      .setSerializationInclusion(NON_EMPTY)
      .enable(ALLOW_UNQUOTED_FIELD_NAMES)
      .enable(ALLOW_FINAL_FIELDS_AS_MUTATORS)
      .disable(WRITE_DATES_AS_TIMESTAMPS)
      .registerModule(Jdk8Module())
      .registerModule(
          KotlinModule(
              nullToEmptyCollection = true,
              nullToEmptyMap = true))
      .registerModule(JavaTimeModule())

  fun create() = m
}