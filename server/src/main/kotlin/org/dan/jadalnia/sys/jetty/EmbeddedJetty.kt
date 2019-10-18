package org.dan.jadalnia.sys.jetty

import com.google.common.collect.Sets
import org.dan.jadalnia.app.user.VolunteerWsListener
import org.dan.jadalnia.app.user.customer.CustomerWsListener
import org.dan.jadalnia.app.ws.WsListener
import org.dan.jadalnia.sys.JerseyConfig
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.glassfish.jersey.servlet.ServletContainer
import org.springframework.web.context.ContextLoaderListener

import java.util.Arrays.asList
import org.dan.jadalnia.sys.AppInitializer.Companion.createWebAppCtx
import org.eclipse.jetty.servlet.ServletContextHandler.NO_SESSIONS
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer.configure
import org.slf4j.LoggerFactory

object EmbeddedJetty {
  private val ports = Sets.newConcurrentHashSet<Int>()
  private val log = LoggerFactory.getLogger(EmbeddedJetty::class.java)

  fun ensureServerRunningOn(port: Int) {
    if (!ports.add(port)) {
      return
    }
    startServerOn(port)
  }

  private fun startServerOn(port: Int) {
    log.info("Start jetty on port {}", port)
    val jettyServer = Server(port)
    val contextHandler = ServletContextHandler(NO_SESSIONS)
    val webAppCtx = createWebAppCtx()
    contextHandler.addEventListener(ContextLoaderListener(webAppCtx))
    contextHandler.contextPath = "/"

    val servletContainer = ServletContainer(JerseyConfig())
    val servletHolder = ServletHolder(servletContainer)

    contextHandler.addServlet(servletHolder, "/*")
    contextHandler.setInitParameter("contextConfigLocation", "")
    jettyServer.handler = contextHandler

    configure(contextHandler) { a, container ->
      for (listener in asList<Class<out WsListener>>(
          CustomerWsListener::class.java,
          VolunteerWsListener::class.java)) {
        container.addEndpoint(listener)
      }
    }

    jettyServer.start()
  }
}
