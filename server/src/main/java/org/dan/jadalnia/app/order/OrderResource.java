package org.dan.jadalnia.app.order;

import lombok.extern.slf4j.Slf4j;
import org.dan.jadalnia.app.auth.AuthService;
import org.dan.jadalnia.util.time.Clocker;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Slf4j
@Path("/")
@Produces(APPLICATION_JSON)
public class OrderResource {
    @Inject
    private AuthService authService;

    @Inject
    private OrderService orderService;

    @Inject
    private Clocker clocker;
}
