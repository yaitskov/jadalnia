package org.dan.jadalnia.app.festival;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.dan.jadalnia.app.festival.menu.MenuItem;
import org.dan.jadalnia.app.festival.pojo.CreatedFestival;
import org.dan.jadalnia.app.festival.pojo.Festival;
import org.dan.jadalnia.app.festival.pojo.FestivalInfo;
import org.dan.jadalnia.app.festival.pojo.FestivalState;
import org.dan.jadalnia.app.festival.pojo.Fid;
import org.dan.jadalnia.app.festival.pojo.NewFestival;
import org.dan.jadalnia.app.user.UserDao;
import org.dan.jadalnia.app.ws.PropertyUpdated;
import org.dan.jadalnia.app.ws.WsBroadcast;
import org.dan.jadalnia.util.collection.AsyncCache;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.dan.jadalnia.app.festival.ctx.FestivalCacheFactory.FESTIVAL_CACHE;
import static org.dan.jadalnia.app.user.UserState.Approved;
import static org.dan.jadalnia.app.user.UserType.Admin;
import static org.dan.jadalnia.sys.ctx.ExecutorCtx.DEFAULT_EXECUTOR;

@Slf4j
@FieldDefaults(makeFinal = true)
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class FestivalService {
    public static final String FESTIVAL_STATE = "festival.state";

    FestivalDao festivalDao;
    UserDao userDao;
    @Named(FESTIVAL_CACHE)
    AsyncCache<Fid, Festival> festivalCache;
    WsBroadcast wsBroadcast;
    @Named(DEFAULT_EXECUTOR)
    ExecutorService executorService;;

    public CompletableFuture<CreatedFestival> create(NewFestival newFestival) {
        return festivalDao
                .create(newFestival)
                .thenCompose(fid -> userDao.register(fid, Admin, Approved,
                        newFestival.getUserName(), newFestival.getUserKey())
                        .thenApply(userSession -> CreatedFestival.builder()
                                .fid(fid)
                                .session(userSession)
                                .build()));
    }

    @SneakyThrows
    public CompletableFuture<Boolean> setState(Festival festival, FestivalState newState) {
        log.info("Update festival state to {}", newState);
        val oldV = festival.getInfo().getAndUpdate(info -> {
            log.info("Update state {} => {} in {}",
                    info.getState(), newState, info.getFid());
            return info.withState(newState);
        });
        festivalDao.setState(festival.getInfo().get().getFid(), newState)
                .thenRunAsync(() -> wsBroadcast.broadcast(festival.getInfo().get().getFid(),
                        PropertyUpdated.builder()
                                .name(FESTIVAL_STATE)
                                .newValue(newState)
                                .build()));
        return completedFuture(oldV.getState() != newState);
    }

    public CompletableFuture<FestivalState> getState(Fid fid) {
        return festivalCache.get(fid)
                .thenApply(Festival::getInfo)
                .thenApply(AtomicReference::get)
                .thenApply(info -> {
                    log.info("Get state {} for {}", info.getState(), info.getFid());
                    return info.getState();
                });
    }

    public CompletableFuture<List<MenuItem>> listMenu(Fid fid) {
        return festivalCache.get(fid)
                .thenApply(Festival::getInfo)
                .thenApply(AtomicReference::get)
                .thenApply(FestivalInfo::getMenu);
    }

    public CompletableFuture<Integer> updateMenu(
            Festival festival, List<MenuItem> items) {
        festival.getInfo().updateAndGet(info -> info.withMenu(items));
        return festivalDao.setMenu(festival.getInfo().get().getFid(), items);
    }
}
