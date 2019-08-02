package org.dan.jadalnia.app.auth.ctx;

import org.dan.jadalnia.app.auth.AuthResource;
import org.dan.jadalnia.app.auth.AuthService;
import org.dan.jadalnia.app.auth.CheckSysAdminSessionResource;
import org.dan.jadalnia.app.auth.CheckUserSessionResource;
import org.dan.jadalnia.app.auth.HelloResource;
import org.dan.jadalnia.app.auth.SecureSessionGenerator;
import org.springframework.context.annotation.Import;

import java.security.SecureRandom;

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
