package org.dan.jadalnia.app.auth.ctx

import org.dan.jadalnia.app.auth.HelloResource
import org.dan.jadalnia.app.auth.SecureSessionGenerator
import org.springframework.context.annotation.Import

import java.security.SecureRandom

@Import(
        SecureRandom::class,
        UserCacheLoader::class,
        UserCacheFactory::class,
        SecureSessionGenerator::class,
        HelloResource::class
)
class AuthCtx
