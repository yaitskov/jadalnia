package org.dan.jadalnia.sys.error;

import org.slf4j.LoggerFactory
import java.util.Collections.list
import javax.ws.rs.core.Response.status

import javax.servlet.http.HttpServletRequest
import javax.ws.rs.core.Context
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper

class JadExMapper : ExceptionMapper<JadEx> {
    @Context
    var request: HttpServletRequest? = null;

    fun req(): HttpServletRequest = request as HttpServletRequest

    companion object {
        val log = LoggerFactory.getLogger(JadExMapper::class.java)
    }

    override fun toResponse(e: JadEx): Response {
        if (request == null) {
            log.error("Background exception [{}], status: [{}], eid {}",
                    e.error, e.status, e.error.id, e);
        } else {
            val headers = HashMap<String, String>();
            list(req().getHeaderNames())
                    .forEach({name -> headers.put(name, req().getHeader(name))});
            log.error("Url exception [{}], eid {}, query: [{}], headers: [{}], status: [{}], message: [{}]",
                    req().getRequestURI(),
                    e.error.id,
                    req().getQueryString(),
                    headers,
                    e.status,
                    e.error,
                    e);
        }
        return status(e.status).entity(e.error).build();
    }
}
