package org.dan.jadalnia.app.ws

import org.slf4j.LoggerFactory
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext

import javax.websocket.server.ServerEndpointConfig

class WsHandlerConfigurator
    : ServerEndpointConfig.Configurator() {

    companion object {
        var _injector: AnnotationConfigWebApplicationContext? = null
        val log = LoggerFactory.getLogger(WsHandlerConfigurator::class.java)

        @JvmStatic
        fun setInjector(injector: AnnotationConfigWebApplicationContext) {
            log.info("Set web socket injector {}", injector)
            _injector = injector
        }

        fun getInjector() = _injector as AnnotationConfigWebApplicationContext
    }

    override fun <T> getEndpointInstance(endpointClass: Class<T>): T {
        try {
            return getInjector()
                    .getAutowireCapableBeanFactory()
                    .createBean(endpointClass)
        } catch (e: Exception) {
            log.error("Failed to instantiate web socket handler {}", endpointClass, e)
            throw InstantiationException("Failed to instantiate a web socket handler")
        }
    }
}
