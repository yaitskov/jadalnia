package org.dan.jadalnia.app.auth.ctx;

import com.google.common.cache.CacheBuilder;
import org.dan.jadalnia.app.auth.AuthResource;
import org.dan.jadalnia.app.auth.AuthService;
import org.dan.jadalnia.app.auth.CheckSysAdminSessionResource;
import org.dan.jadalnia.app.auth.CheckUserSessionResource;
import org.dan.jadalnia.app.auth.HelloResource;
import org.dan.jadalnia.app.auth.SecureSessionGenerator;
import org.dan.jadalnia.app.user.UserDao;
import org.dan.jadalnia.app.user.UserInfo;
import org.dan.jadalnia.app.user.UserSession;
import org.dan.jadalnia.util.collection.AsyncCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Import({SecureRandom.class,
        UserCacheLoader.class,
        UserCacheFactory.class,

        SecureSessionGenerator.class,
        AuthResource.class,
        CheckSysAdminSessionResource.class,
        HelloResource.class,
        CheckUserSessionResource.class,
        AuthService.class})
public class AuthCtx {
}
