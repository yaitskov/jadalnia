package org.dan.jadalnia.app.token

import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.token.TokenBalanceCacheFactory.Companion.TOKEN_BALANCE_CACHE
import org.dan.jadalnia.app.user.Uid
import org.dan.jadalnia.app.user.UserSession
import org.dan.jadalnia.app.user.WithUser
import org.dan.jadalnia.org.dan.jadalnia.app.auth.AuthService.SESSION
import org.dan.jadalnia.util.Futures
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
    val with: WithUser,
    @Named(TOKEN_BALANCE_CACHE)
    val tokenBalanceCache: AsyncCache<Pair<Fid, Uid>, TokenBalance>,
    val tokenService: TokenService) {
  companion object {
    val log = LoggerFactory.getLogger(TokenResource::class.java)
    const val TOKEN = "token/"
    const val REQUEST_TOKEN = TOKEN + "request"
    const val REQUEST_TOKEN_RETURN = TOKEN + "request-return"
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
    with.customerFest(response, session) { festival ->
      val fid = festival.fid()
      log.info("Invalidate balance cache {} in fid {}", session.uid, fid)
      tokenBalanceCache.invalidate(Pair(fid, session.uid))
      Futures.voidF()
    }
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
  @Path("${TOKEN}request-kasier-history/{page}")
  fun kasierRequestHistory(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession,
      @PathParam("page")
      page: Int) {
    with.kasierFest(response, session) { festival ->
      tokenService.listKasierHistory(session.uid, festival, page, 100)
    }
  }

  @GET
  @Path(TOKEN + "visitor-view/{tokReq}")
  fun showVisitorTokenRequest(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession,
      @PathParam("tokReq")
      tokenReqId: TokenId) {
    with.customerFest(response, session) { festival ->
      tokenService.showVisitorTokenRequest(festival.fid(), tokenReqId)
    }
  }

  @POST
  @Path("token/cancel-approved/{requestId}")
  fun cancelApproved(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession,
      @PathParam("requestId") requestId: TokenId) {
    with.kasierFest(response, session) { festival ->
      tokenService.cancelApproved(festival, requestId, session.uid)
    }
  }

  @GET
  @Path("token/cashier-view/{requestId}")
  fun showRequestToCashier(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession,
      @PathParam("requestId") requestId: TokenId) {
    with.kasierFest(response, session) { festival ->
      tokenService.showRequestToCashier(festival, requestId)
    }
  }

  @POST
  @Path(REQUEST_TOKEN)
  fun customerRequests(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession,
      amount: TokenPoints) {
    with.customerFest(response, session) { festival ->
      tokenService.requestTokensPurchase(
          festival, session.uid, amount, TokenOp.Buy)
    }
  }

  @POST
  @Path(REQUEST_TOKEN_RETURN + "/{amount}")
  fun customerRequestsTokenReturnNoBody(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession,
      @PathParam("amount") amount: TokenPoints) {
    customerRequestsTokenReturn(response, session, amount)
  }

  @POST
  @Path(REQUEST_TOKEN_RETURN)
  fun customerRequestsTokenReturn(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession,
      amount: TokenPoints) {
    with.customerFest(response, session) { festival ->
      tokenService.requestTokensPurchase(
          festival, session.uid, amount, TokenOp.Sel)
    }
  }

  @GET
  @Path(LIST_REQUESTS_FOR_APPROVE + "/{customerUid}")
  fun kasierApprovesRequests(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession,
      @PathParam("customerUid")
      customer: Uid) {
    with.kasierFest(response, session) { festival ->
      tokenService.findTokensForApprove(festival, customer)
    }
  }

  @GET
  @Path(GET_MY_BALANCE)
  fun getMyBalance(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession) {
    with.customerFest(response, session) { festival ->
      tokenService.getBalance(festival, session.uid)
    }
  }

  @POST
  @Path(APPROVE_REQUEST)
  fun kasierApprovesRequests(
      @Suspended response: AsyncResponse,
      @HeaderParam(SESSION) session: UserSession,
      approveReq: TokensApproveReq) {
    with.kasierFest(response, session) { festival ->
      tokenService.approveTokens(festival, session.uid, approveReq)
    }
  }
}