package org.dan.jadalnia.app.festival;

import lombok.extern.slf4j.Slf4j;
import org.dan.jadalnia.app.festival.menu.MenuItem;
import org.dan.jadalnia.app.festival.pojo.CreatedFestival;
import org.dan.jadalnia.app.festival.pojo.Festival;
import org.dan.jadalnia.app.festival.pojo.FestivalState;
import org.dan.jadalnia.app.festival.pojo.Fid;
import org.dan.jadalnia.app.festival.pojo.NewFestival;
import org.dan.jadalnia.app.user.UserInfo;
import org.dan.jadalnia.app.user.UserSession;
import org.dan.jadalnia.util.collection.AsyncCache;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.dan.jadalnia.app.auth.ctx.UserCacheFactory.USER_SESSIONS;
import static org.dan.jadalnia.app.auth.AuthService.SESSION;
import static org.dan.jadalnia.app.festival.ctx.FestivalCacheFactory.FESTIVAL_CACHE;

@Slf4j
@Path("/")
@Produces(APPLICATION_JSON)
public class FestivalResource {
    private static final String FESTIVAL = "/festival/";
    public static final String FESTIVAL_STATE = FESTIVAL + "state";
    public static final String FESTIVAL_CREATE = FESTIVAL + "create";
    public static final String INVALIDATE_CACHE = FESTIVAL + "invalidate/cache";

    @Inject
    private FestivalService festivalService;
    
    @Inject
    @Named(USER_SESSIONS)
    private AsyncCache<UserSession, UserInfo> userSessions;

    @Inject
    @Named(FESTIVAL_CACHE)
    private AsyncCache<Fid, Festival> festivalCache;

    @POST
    @Path(FESTIVAL_CREATE)
    @Consumes(APPLICATION_JSON)
    public CompletableFuture<CreatedFestival> create(NewFestival newFestival) {
        return festivalService.create(newFestival);
    }

    @POST
    @Path(FESTIVAL_STATE)
    @Consumes(APPLICATION_JSON)
    public CompletableFuture<Void> setState(
            @HeaderParam(SESSION) UserSession session,
            FestivalState state) {
        return userSessions.get(session)
                .thenApply(user -> user.ensureAdmin().getFid())
                .thenCompose(festivalCache::get)
                .thenCompose(festival -> festivalService.setState(festival, state));
    }

    @GET
    @Path(FESTIVAL + "/menu/" + "{fid}")
    public CompletableFuture<List<MenuItem>> listMenu(@PathParam("fid") Fid fid) {
        return festivalService.listMenu(fid);
    }

    @POST
    @Path(FESTIVAL + "/menu")
    public CompletableFuture<Integer> updateMenu(
            @HeaderParam(SESSION) UserSession session,
            List<MenuItem> items) {
        return userSessions.get(session).thenApply(user -> user.ensureAdmin().getFid())
                .thenCompose(fid -> festivalCache.get(fid))
                .thenCompose(festival -> festivalService.updateMenu(festival, items));
    }

    @POST
    @Path(INVALIDATE_CACHE)
    @Consumes(APPLICATION_JSON)
    public CompletableFuture<Void> invalidateCache(
            @HeaderParam(SESSION) UserSession session) {
        return userSessions.get(session)
                .thenApply(user -> user.ensureAdmin().getFid())
                .thenAccept(fid -> festivalCache.invalidate(fid));
    }
}
