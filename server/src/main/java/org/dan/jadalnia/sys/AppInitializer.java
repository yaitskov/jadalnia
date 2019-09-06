package org.dan.jadalnia.sys;

import lombok.extern.slf4j.Slf4j;
import org.dan.jadalnia.app.ws.WsHandlerConfigurator;
import org.dan.jadalnia.sys.ctx.AppContext;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.core.annotation.Order;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import javax.servlet.ServletContext;

import static java.util.Collections.singletonList;

@Slf4j
@Order(1)
public class AppInitializer implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext servletContext) {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        final WebApplicationContext webAppCtx = createWebAppCtx(
                singletonList(AppContext.class));
        servletContext.addListener(new ContextLoaderListener(webAppCtx));
        servletContext.setInitParameter("contextConfigLocation", "");
    }

    public static WebApplicationContext createWebAppCtx(Iterable<Class<?>> configClasses) {
        final AnnotationConfigWebApplicationContext context
                = new AnnotationConfigWebApplicationContext();
        WsHandlerConfigurator.setInjector(context);
        configClasses.forEach(context::register);
        return context;
    }
}
