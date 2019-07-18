package org.dan.jadalnia.app.festival;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.dan.jadalnia.app.user.UserDao;
import org.dan.jadalnia.app.user.UserState;
import org.dan.jadalnia.app.user.UserType;
import org.dan.jadalnia.app.ws.PropertyUpdated;
import org.dan.jadalnia.app.ws.WsBroadcast;
import org.dan.jadalnia.util.collection.AsyncCache;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Named;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import static org.dan.jadalnia.app.festival.FestivalCache.FESTIVAL_CACHE;
import static org.dan.jadalnia.sys.db.DbContext.TRANSACTION_MANAGER;

@Slf4j
@FieldDefaults(makeFinal = true)
@RequiredArgsConstructor
public class FestivalService {
    FestivalDao festivalDao;
    UserDao userDao;
    @Named(FESTIVAL_CACHE)
    AsyncCache<Fid, Festival> festivalCache;
    WsBroadcast wsBroadcast;

    @Transactional(TRANSACTION_MANAGER)
    public CreatedFestival create(NewFestival newFestival) {
        val fid = festivalDao.create(newFestival);
        return CreatedFestival
                .builder()
                .fid(fid)
                .session(userDao.register(fid, UserType.Admin, UserState.Approved,
                        newFestival.getUserName(), newFestival.getUserKey()))
                .build();
    }

    @SneakyThrows
    public CompletableFuture<Void> setState(Festival festival, FestivalState newState) {
        festival.getInfo().updateAndGet(info -> info.withState(newState));
        return festivalDao.setState(festival.getInfo().get().getFid(), newState)
                .thenRunAsync(() -> wsBroadcast.broadcast(festival,
                        PropertyUpdated.builder()
                                .name("festival.state")
                                .newValue(newState)
                                .build()));
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
