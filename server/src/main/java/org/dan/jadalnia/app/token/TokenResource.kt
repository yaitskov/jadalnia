package org.dan.jadalnia.app.token

import org.dan.jadalnia.app.auth.ctx.UserCacheFactory.Companion.USER_SESSIONS
import org.dan.jadalnia.app.festival.ctx.FestivalCacheFactory.Companion.FESTIVAL_CACHE
import org.dan.jadalnia.app.festival.pojo.Festival
import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.token.TokenBalanceCacheFactory.Companion.TOKEN_BALANCE_CACHE
import org.dan.jadalnia.app.user.Uid
import org.dan.jadalnia.app.user.UserInfo
import org.dan.jadalnia.app.user.UserSession
import org.dan.jadalnia.org.dan.jadalnia.app.auth.AuthService.SESSION
import org.dan.jadalnia.sys.async.AsynSync
import org.dan.jadalnia.util.collection.AsyncCache
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Named
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.HeaderParam
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.container.AsyncResponse
import javax.ws.rs.container.Suspended
import javax.ws.rs.core.MediaType.APPLICATION_JSON

@Path("/")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
class TokenResource @Inject constructor(
    @Named(TOKEN_BALANCE_CACHE)
    val tokenBalanceCache: AsyncCache<Pair<Fid, Uid>, TokenBalance>,
    val tokenService: TokenService,
    @Named(USER_SESSIONS)
    val userSessions: AsyncCache<UserSession, UserInfo>,
    @Named(FESTIVAL_CACHE)
    val festivalCache: AsyncCache<Fid, Festival>,
    val asynSync: AsynSync) {
  companion object {
    val log = LoggerFactory.getLogger(TokenResource::class.java)
    const val TOKEN = "token/"
    const val REQUEST_TOKEN = TOKEN + "request"
    const val APPROVE_REQUEST = TOKEN + "approve"
    const val LIST_REQUESTS_FOR_APPROVE = TOKEN + "list-for-approve"
    const val GET_MY_BALANCE = TOKEN + "myBalance"
    const val INVALIDATE_BALANCE_CACHE = TOKEN + "invalidateBalanceCache"
  }

  @POST
  @Path(INVALIDATE_BALANCE_CACHE)
  fun invalidateBalanceCache(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession) {
    asynSync.sync(
        userSessions.get(session)
            .thenApply { user -> user.ensureCustomer().fid }
            .thenAccept { fid ->
              log.info("Invalidate balance cache {} in fid {}", session.uid, fid)
              tokenBalanceCache.invalidate(Pair(fid, session.uid))
            },
        response)
  }

  @POST
  @Path(REQUEST_TOKEN + "/{amount}")
  fun customerRequestsVoidBody(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession,
      @PathParam("amount")
      amount: TokenPoints) {
    customerRequests(response, session, amount);
  }

  @GET
  @Path(TOKEN + "visitor-view/{tokReq}")
  fun showVisitorTokenRequest(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession,
      @PathParam("tokReq")
      tokenReqId: TokenId) {
    asynSync.sync(
        userSessions.get(session)
            .thenApply { user -> user.ensureCustomer().fid }
            .thenCompose(festivalCache::get)
            .thenCompose { festival ->
              tokenService.showVisitorTokenRequest(festival.info.get().fid, tokenReqId)
            },
        response)
  }

  @POST
  @Path(REQUEST_TOKEN)
  fun customerRequests(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession,
      amount: TokenPoints) {
    asynSync.sync(
        userSessions.get(session)
            .thenApply { user -> user.ensureCustomer().fid }
            .thenCompose(festivalCache::get)
            .thenCompose { festival ->
              tokenService.requestTokens(festival, session.uid, amount)
            },
        response)
  }

  @GET
  @Path(LIST_REQUESTS_FOR_APPROVE + "/{customerUid}")
  fun kasierApprovesRequests(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession,
      @PathParam("customerUid")
      customer: Uid) {
    asynSync.sync(
        userSessions.get(session)
            .thenApply { user -> user.ensureKasier().fid }
            .thenCompose(festivalCache::get)
            .thenCompose { festival ->
              tokenService.findTokensForApprove(festival, customer)
            },
        response)
  }

  @GET
  @Path(GET_MY_BALANCE)
  fun getMyBalance(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) customerSession: UserSession) {
    asynSync.sync(
        userSessions.get(customerSession)
            .thenApply { user -> user.ensureCustomer().fid }
            .thenCompose(festivalCache::get)
            .thenCompose { festival ->
              tokenService.getBalance(festival, customerSession.uid)
            },
        response)
  }

  @POST
  @Path(APPROVE_REQUEST)
  fun kasierApprovesRequests(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession,
      approveReq: TokensApproveReq) {
    asynSync.sync(
        userSessions.get(session)
            .thenApply { user -> user.ensureKasier().fid }
            .thenCompose(festivalCache::get)
            .thenCompose { festival ->
              tokenService.approveTokens(festival, session.uid, approveReq)
            },
        response)
  }
}