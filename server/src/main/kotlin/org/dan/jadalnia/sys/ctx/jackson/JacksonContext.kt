package org.dan.jadalnia.sys.ctx.jackson

import org.springframework.context.annotation.Import

@Import(ObjectMapperProvider::class, ObjectMapperContextResolver::class)
class JacksonContext
