package org.dan.jadalnia.sys

import lombok.extern.slf4j.Slf4j
import org.dan.jadalnia.app.ws.WsHandlerConfigurator
import org.dan.jadalnia.sys.ctx.AppContext
import org.slf4j.bridge.SLF4JBridgeHandler
import org.springframework.core.annotation.Order
import org.springframework.web.WebApplicationInitializer
import org.springframework.web.context.ContextLoaderListener
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext

import javax.servlet.ServletContext


@Slf4j
@Order(1)
class AppInitializer : WebApplicationInitializer {
    override fun onStartup(servletContext: ServletContext) {
        SLF4JBridgeHandler.removeHandlersForRootLogger()
        SLF4JBridgeHandler.install()
        servletContext.addListener(ContextLoaderListener(createWebAppCtx()))
        servletContext.setInitParameter("contextConfigLocation", "")
    }

    companion object {
        @JvmStatic
        fun createWebAppCtx(): WebApplicationContext {
            val context = AnnotationConfigWebApplicationContext()
            WsHandlerConfigurator.setInjector(context)
            context.register(AppContext::class.java)
            return context
        }
    }
}
