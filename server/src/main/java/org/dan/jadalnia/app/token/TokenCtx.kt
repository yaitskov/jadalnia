package org.dan.jadalnia.app.token

import org.springframework.context.annotation.Import

@Import(TokenDao::class,
    TokenResource::class,
    TokenBalanceCacheLoader::class,
    TokenBalanceCacheFactory::class,
    TokenService::class)
class TokenCtx