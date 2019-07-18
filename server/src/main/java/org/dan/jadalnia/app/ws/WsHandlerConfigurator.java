package org.dan.jadalnia.app.ws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import javax.websocket.server.ServerEndpointConfig;

@Slf4j
public class WsHandlerConfigurator extends ServerEndpointConfig.Configurator {
    private static volatile AnnotationConfigWebApplicationContext injector;

    public static void setInjector(AnnotationConfigWebApplicationContext injector) {
        log.info("Set web socket injector {}", injector);
        WsHandlerConfigurator.injector = injector;
    }

    @Override
    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
        try {
            return injector.getAutowireCapableBeanFactory().createBean(endpointClass);
        } catch (Exception e) {
            log.error("Failed to instantiate web socket handler {}", endpointClass, e);
            throw new InstantiationException("Failed to instantiate a web socket handler");
        }
    }

}
