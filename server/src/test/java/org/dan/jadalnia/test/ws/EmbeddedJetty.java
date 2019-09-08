package org.dan.jadalnia.test.ws;

import com.google.common.collect.Sets;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.dan.jadalnia.app.user.customer.CustomerWsListener;
import org.dan.jadalnia.sys.JerseyConfig;
import org.dan.jadalnia.sys.ctx.AppContext;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.glassfish.jersey.servlet.ServletContainer;
import org.springframework.web.context.ContextLoaderListener;

import java.util.Set;

import static java.util.Collections.singletonList;
import static org.dan.jadalnia.sys.AppInitializer.createWebAppCtx;
import static org.eclipse.jetty.servlet.ServletContextHandler.NO_SESSIONS;

@Slf4j
public class EmbeddedJetty {
    public static final EmbeddedJetty EMBEDDED_JETTY = new EmbeddedJetty();

    private final Set<Integer> ports = Sets.newConcurrentHashSet();

    public void ensureServerRunningOn(int port) {
        if (!ports.add(port)) {
            return;
        }
        startServerOn(port);
    }

    @SneakyThrows
    void startServerOn(int port) {
        log.info("Start jetty on port {}", port);
        val jettyServer = new Server(port);
        val contextHandler = new ServletContextHandler(NO_SESSIONS);
        val webAppCtx = createWebAppCtx(singletonList(AppContext.class));
        contextHandler.addEventListener(new ContextLoaderListener(webAppCtx));
        contextHandler.setContextPath("/");

//        val resourceConfig = new ResourceConfig(FestivalResource.class, UserResource.class);
//        resourceConfig.register(JacksonFeature.class);

        val servletContainer = new ServletContainer(new JerseyConfig());
        val servletHolder = new ServletHolder(servletContainer);

        contextHandler.addServlet(servletHolder, "/*");
        contextHandler.setInitParameter("contextConfigLocation", "");
        jettyServer.setHandler(contextHandler);

        WebSocketServerContainerInitializer.configure(
                contextHandler,
                (a, container) -> {
                    container.addEndpoint(CustomerWsListener.class);
                });

        jettyServer.start();
    }
}
