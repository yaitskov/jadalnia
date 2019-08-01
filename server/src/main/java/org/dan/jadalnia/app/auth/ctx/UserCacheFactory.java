package org.dan.jadalnia.app.auth.ctx;

import com.google.common.cache.CacheBuilder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.dan.jadalnia.app.user.UserInfo;
import org.dan.jadalnia.app.user.UserSession;
import org.dan.jadalnia.util.collection.AsyncCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import javax.inject.Inject;

import static java.util.concurrent.TimeUnit.SECONDS;

@FieldDefaults(makeFinal = true)
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class UserCacheFactory {
    public static final String USER_SESSIONS = "user_sessions";

    UserCacheLoader loader;

    @Value("${expire.user.session.seconds}")
    private int expireUserSessionSeconds;

    @Bean(name = USER_SESSIONS)
    public AsyncCache<UserSession, UserInfo> create() {
        return new AsyncCache<>(
                CacheBuilder.newBuilder()
                        .expireAfterAccess(expireUserSessionSeconds, SECONDS)
                        .build(loader));
    }
}
