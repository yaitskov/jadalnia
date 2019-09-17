package org.dan.jadalnia.app.user

import org.springframework.context.annotation.Import

@Import(UserResource::class, UserService::class, UserDao::class)
class UserCtx
