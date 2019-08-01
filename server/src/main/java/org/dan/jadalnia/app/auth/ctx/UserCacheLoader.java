package org.dan.jadalnia.app.auth.ctx;

import com.google.common.cache.CacheLoader;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.dan.jadalnia.app.user.UserDao;
import org.dan.jadalnia.app.user.UserInfo;
import org.dan.jadalnia.app.user.UserSession;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;


@Slf4j
@FieldDefaults(makeFinal = true)
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class UserCacheLoader extends CacheLoader<UserSession, CompletableFuture<UserInfo>>  {
    UserDao userDao;

    @Override
    public CompletableFuture<UserInfo> load(UserSession sessionKey) {
        return userDao.getUserBySession(sessionKey);
    }
}
