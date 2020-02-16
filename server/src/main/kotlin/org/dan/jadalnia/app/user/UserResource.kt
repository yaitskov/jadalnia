package org.dan.jadalnia.app.user

import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.sys.async.AsynSync
import javax.inject.Inject
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.container.AsyncResponse
import javax.ws.rs.container.Suspended
import javax.ws.rs.core.MediaType.APPLICATION_JSON

@Path("/")
@Produces(APPLICATION_JSON)
class UserResource @Inject constructor(
    val userService: UserService,
    val asynSync: AsynSync) {
  companion object {
    const val USER = "user/"
    const val REGISTER = USER + "register"
    const val LIST = "${USER}list/{fid}/type/{type}"
  }

  @POST
  @Path(REGISTER)
  @Consumes(APPLICATION_JSON)
  fun register(
      @Suspended response: AsyncResponse,
      regRequest: UserRegRequest) {
    asynSync.sync(userService.register(regRequest), response)
  }

  @GET
  @Path(LIST)
  fun listByType(
      @Suspended response: AsyncResponse,
      @PathParam("fid") fid: Fid,
      @PathParam("type") userType: UserType) {
    asynSync.sync(userService.listByType(fid, userType), response)
  }
}
