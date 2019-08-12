package org.dan.jadalnia.app.festival;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.dan.jadalnia.app.festival.menu.MenuItem;
import org.dan.jadalnia.app.festival.pojo.Festival;
import org.dan.jadalnia.app.festival.pojo.FestivalState;
import org.dan.jadalnia.app.festival.pojo.Fid;
import org.dan.jadalnia.app.festival.pojo.NewFestival;
import org.dan.jadalnia.app.user.UserInfo;
import org.dan.jadalnia.app.user.UserSession;
import org.dan.jadalnia.sys.async.AsynSync;
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
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.dan.jadalnia.app.auth.AuthService.SESSION;
import static org.dan.jadalnia.app.auth.ctx.UserCacheFactory.USER_SESSIONS;
import static org.dan.jadalnia.app.festival.ctx.FestivalCacheFactory.FESTIVAL_CACHE;

@Slf4j
@Path("/")
@Produces(APPLICATION_JSON)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class FestivalResource {
    private static final String FESTIVAL = "/festival/";
    public static final String FESTIVAL_MENU = FESTIVAL + "menu";
    public static final String FESTIVAL_STATE = FESTIVAL + "state";
    public static final String FESTIVAL_CREATE = FESTIVAL + "create";
    public static final String INVALIDATE_CACHE = FESTIVAL + "invalidate/cache";

    FestivalService festivalService;
    
    @Named(USER_SESSIONS)
    AsyncCache<UserSession, UserInfo> userSessions;

    @Named(FESTIVAL_CACHE)
    AsyncCache<Fid, Festival> festivalCache;

    AsynSync asynSync;

    @POST
    @Path(FESTIVAL_CREATE)
    @Consumes(APPLICATION_JSON)
    public void create(
            @Suspended AsyncResponse response,
            NewFestival newFestival) {
        asynSync.sync(festivalService.create(newFestival), response);
    }

    @POST
    @Path(FESTIVAL_STATE)
    @Consumes(APPLICATION_JSON)
    public void setState(
            @Suspended AsyncResponse response,
            @HeaderParam(SESSION) UserSession session,
            FestivalState state) {
        asynSync.sync(
                userSessions.get(session)
                        .thenApply(user -> user.ensureAdmin().getFid())
                        .thenCompose(festivalCache::get)
                        .thenCompose(festival -> festivalService.setState(festival, state)),
                response);
    }

    @GET
    @Path(FESTIVAL_STATE + "/{fid}")
    public void getState(
            @Suspended AsyncResponse response,
            @PathParam("fid") Fid fid) {
        asynSync.sync(festivalService.getState(fid), response);
    }

    @GET
    @Path(FESTIVAL_MENU + "/{fid}")
    public void listMenu(
            @Suspended AsyncResponse response,
            @PathParam("fid") Fid fid) {
        asynSync.sync(festivalService.listMenu(fid), response);
    }

    @POST
    @Path(FESTIVAL_MENU)
    public void updateMenu(
            @Suspended AsyncResponse response,
            @HeaderParam(SESSION) UserSession session,
            List<MenuItem> items) {
        asynSync.sync(
                userSessions.get(session)
                        .thenApply(user -> user.ensureAdmin().getFid())
                        .thenCompose(festivalCache::get)
                        .thenCompose(festival -> festivalService.updateMenu(festival, items)),
                response);
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
