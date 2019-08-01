package org.dan.jadalnia.app.user;

import lombok.extern.slf4j.Slf4j;
import org.dan.jadalnia.app.auth.AuthService;
import org.dan.jadalnia.util.collection.AsyncCache;
import org.dan.jadalnia.util.time.Clocker;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Path;

import static org.dan.jadalnia.app.auth.ctx.UserCacheFactory.USER_SESSIONS;

@Slf4j
@Path("/")
public class UserResource {
    static final String USER_REGISTER = "/anonymous/user/register";
    public static final String OFFLINE_USER_REGISTER = "/anonymous/offline-user/register";
    static final String USER_INFO_BY_SESSION = "/anonymous/user/info/by/session";

    @Inject
    private UserDao userDao;

    @Inject
    private AuthService authService;

    @Inject
    @Named(USER_SESSIONS)
    private AsyncCache<UserSession, UserInfo> userSessions;



    @Inject
    private Clocker clocker;

//
//    @Inject
//    @Named(USER_SESSIONS)
//    private AsyncCache<UserSession, UserInfo> userSessions;

}
