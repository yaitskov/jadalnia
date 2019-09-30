package org.dan.jadalnia.app.token

import org.springframework.context.annotation.Import

@Import(TokenDao::class, TokenResource::class, TokenService::class)
class TokenCtx