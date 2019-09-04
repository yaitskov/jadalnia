package org.dan.jadalnia.app.user;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.dan.jadalnia.app.auth.AuthService;
import org.dan.jadalnia.app.festival.pojo.Festival;
import org.dan.jadalnia.app.festival.pojo.Fid;
import org.dan.jadalnia.sys.async.AsynSync;
import org.dan.jadalnia.util.collection.AsyncCache;
import org.dan.jadalnia.util.time.Clocker;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.dan.jadalnia.app.auth.ctx.UserCacheFactory.USER_SESSIONS;
import static org.dan.jadalnia.app.festival.ctx.FestivalCacheFactory.FESTIVAL_CACHE;

@Slf4j
@Path("/")
@Produces(APPLICATION_JSON)
@FieldDefaults(makeFinal = true)
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class UserResource {
    private static final String USER = "user/";
    public static final String REGISTER = USER + "register";

    UserDao userDao;
    AuthService authService;
    Clocker clocker;

    @Named(USER_SESSIONS)
    AsyncCache<UserSession, UserInfo> userSessions;
    @Named(FESTIVAL_CACHE)
    AsyncCache<Fid, Festival> festivalCache;

    UserService userService;
    AsynSync asynSync;

    @POST
    @Path(REGISTER)
    @Consumes(APPLICATION_JSON)
    public void register(
            @Suspended AsyncResponse response,
            UserRegRequest regRequest) {
        asynSync.sync(userService.register(regRequest), response);
    }
}
