package org.dan.jadalnia.app.auth

import javax.ws.rs.GET
import javax.ws.rs.Path

@Path("/")
class HelloResource {
    @GET
    @Path("/hello")
    fun hello() = "hello"
}
