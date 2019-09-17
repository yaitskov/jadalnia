package org.dan.jadalnia.app.user

import org.dan.jadalnia.app.festival.pojo.Festival
import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.sys.async.AsynSync
import org.dan.jadalnia.util.collection.AsyncCache
import org.dan.jadalnia.util.time.Clocker

import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.container.AsyncResponse
import javax.ws.rs.container.Suspended

import javax.ws.rs.core.MediaType.APPLICATION_JSON
import org.dan.jadalnia.app.auth.ctx.UserCacheFactory.Companion.USER_SESSIONS
import org.dan.jadalnia.app.festival.ctx.FestivalCacheFactory.Companion.FESTIVAL_CACHE

@Path("/")
@Produces(APPLICATION_JSON)
class UserResource @Inject constructor(
        val userDao: UserDao,
        val clocker: Clocker,
        @Named(USER_SESSIONS)
        val userSessions: AsyncCache<UserSession, UserInfo>,
        @Named(FESTIVAL_CACHE)
        val festivalCache: AsyncCache<Fid, Festival>,
        val userService: UserService,
        val asynSync: AsynSync)
{
    companion object {
        const val USER = "user/"
        const val REGISTER = USER + "register"
    }

    @POST
    @Path(REGISTER)
    @Consumes(APPLICATION_JSON)
    fun register(
            @Suspended response: AsyncResponse,
            regRequest: UserRegRequest) {
        asynSync.sync(userService.register(regRequest), response)
    }
}
