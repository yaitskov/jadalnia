package org.dan.jadalnia.app.auth.ctx

import com.google.common.cache.CacheBuilder
import org.dan.jadalnia.app.user.UserInfo
import org.dan.jadalnia.app.user.UserSession
import org.dan.jadalnia.util.collection.AsyncCache
import org.springframework.context.annotation.Bean

import javax.inject.Inject

class UserCacheFactory @Inject constructor(val loader: UserCacheLoader) {
    companion object {
        const val USER_SESSIONS = "user_sessions"
    }

    @Bean(name = [USER_SESSIONS])
    fun create() = AsyncCache<UserSession, UserInfo>(
            CacheBuilder.newBuilder().build(loader))
}
