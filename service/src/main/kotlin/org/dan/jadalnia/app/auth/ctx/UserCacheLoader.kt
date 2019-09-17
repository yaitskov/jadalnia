package org.dan.jadalnia.app.auth.ctx

import com.google.common.cache.CacheLoader
import org.dan.jadalnia.app.user.UserDao
import org.dan.jadalnia.app.user.UserInfo
import org.dan.jadalnia.app.user.UserSession

import javax.inject.Inject
import java.util.concurrent.CompletableFuture

class UserCacheLoader @Inject constructor(val userDao: UserDao)
    : CacheLoader<UserSession, CompletableFuture<UserInfo>>() {

    override fun load(sessionKey: UserSession)
            = userDao.getUserBySession(sessionKey)
}
