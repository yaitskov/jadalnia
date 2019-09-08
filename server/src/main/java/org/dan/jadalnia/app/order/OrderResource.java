package org.dan.jadalnia.app.order;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.dan.jadalnia.app.festival.pojo.Festival;
import org.dan.jadalnia.app.festival.pojo.Fid;
import org.dan.jadalnia.app.order.pojo.OrderItem;
import org.dan.jadalnia.app.user.UserInfo;
import org.dan.jadalnia.app.user.UserSession;
import org.dan.jadalnia.sys.async.AsynSync;
import org.dan.jadalnia.util.collection.AsyncCache;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.dan.jadalnia.app.auth.AuthService.SESSION;
import static org.dan.jadalnia.app.auth.ctx.UserCacheFactory.USER_SESSIONS;
import static org.dan.jadalnia.app.festival.ctx.FestivalCacheFactory.FESTIVAL_CACHE;

@Slf4j
@Path("/")
@Produces(APPLICATION_JSON)
@FieldDefaults(makeFinal = true)
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class OrderResource {
    public static final String ORDER = "order/";
    public static final String PUT_ORDER = ORDER + "put";

    OrderService orderService;

    @Named(USER_SESSIONS)
    AsyncCache<UserSession, UserInfo> userSessions;

    @Named(FESTIVAL_CACHE)
    AsyncCache<Fid, Festival> festivalCache;

    AsynSync asynSync;

    @POST
    @Path(PUT_ORDER)
    @Consumes(APPLICATION_JSON)
    public void create(
            @Suspended AsyncResponse response,
            @HeaderParam(SESSION) UserSession session,
            List<OrderItem> newOrderItems) {
        asynSync.sync(
                userSessions.get(session)
                        .thenApply(user -> user.ensureCustomer().getFid())
                        .thenCompose(festivalCache::get)
                        .thenCompose(festival -> orderService.putNewOrder(
                                festival, session, newOrderItems)), response);
    }
}
