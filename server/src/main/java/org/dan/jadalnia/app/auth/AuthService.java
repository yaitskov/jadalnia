package org.dan.jadalnia.app.auth;

import lombok.extern.slf4j.Slf4j;
import org.dan.jadalnia.app.user.UserDao;
import org.dan.jadalnia.app.user.UserInfo;
import org.dan.jadalnia.app.user.UserSession;
import org.dan.jadalnia.util.collection.AsyncCache;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;
import javax.inject.Named;

import static org.dan.jadalnia.app.auth.ctx.UserCacheFactory.USER_SESSIONS;

@Slf4j
public class AuthService {
    public static final int USER_PART_SESSION_LEN = 20;
    public static final String SESSION = "session";
    private static final String SIGN_IN_LINK_PARAM = "sign-in-link";
    private static final String SIGN_IN_LINK_EMAIL_TEMPLATE = "sign-in-link";

    @Inject
    private UserDao userDao;

    @Inject
    @Named(USER_SESSIONS)
    private AsyncCache<UserSession, UserInfo> userSessions;

    @Value("${base.site.url}")
    private String baseSiteUrl;
 }
