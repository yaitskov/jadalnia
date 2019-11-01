package org.dan.jadalnia.app.push

import org.dan.jadalnia.app.auth.ctx.UserCacheFactory.Companion.USER_SESSIONS
import org.dan.jadalnia.app.user.UserInfo
import org.dan.jadalnia.app.user.UserSession
import org.dan.jadalnia.org.dan.jadalnia.app.auth.AuthService.SESSION
import org.dan.jadalnia.sys.async.AsynSync
import org.dan.jadalnia.util.collection.AsyncCache
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Consumes
import javax.ws.rs.HeaderParam
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.container.AsyncResponse
import javax.ws.rs.container.Suspended
import javax.ws.rs.core.MediaType.APPLICATION_JSON

@Path("/push")
@Produces(APPLICATION_JSON)
class WebPushResource @Inject constructor(
    @Named(USER_SESSIONS)
    val userSessions: AsyncCache<UserSession, UserInfo>,
    val webPushService: WebPushService,
    val asyncSync: AsynSync) {

  @POST
  @Path("register")
  @Consumes(APPLICATION_JSON)
  fun registerTopic(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession,
      subscription: PushSubscription) {
    asyncSync.sync(
        userSessions.get(session)
            .thenCompose {user -> webPushService.registerPushTopic(user, subscription)},
        response)
  }
}