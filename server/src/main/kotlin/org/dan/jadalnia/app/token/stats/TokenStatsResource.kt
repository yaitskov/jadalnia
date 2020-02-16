package org.dan.jadalnia.app.order.stats

import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.user.WithUser
import javax.inject.Inject
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.container.AsyncResponse
import javax.ws.rs.container.Suspended
import javax.ws.rs.core.MediaType.APPLICATION_JSON

@Path("/")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
class TokenStatsResource @Inject constructor(
    val with: WithUser,
    val orderStats: TokenStatsService) {

  @GET
  @Path("token-stats/{fid}")
  fun tokenStats(
      @Suspended response: AsyncResponse,
      @PathParam("fid") fid: Fid) {
    with.anonymous(response, orderStats.tokenStats(fid))
  }
}