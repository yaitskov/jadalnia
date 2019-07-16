package org.dan.jadalnia.app.festival;

import static org.dan.jadalnia.app.festival.FestivalCache.FESTIVAL_CACHE;
import static org.dan.jadalnia.sys.db.DbContext.TRANSACTION_MANAGER;

import com.google.common.cache.LoadingCache;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.dan.jadalnia.app.user.UserDao;
import org.dan.jadalnia.app.user.UserState;
import org.dan.jadalnia.app.user.UserType;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;
import javax.inject.Named;

@Slf4j
public class FestivalService {
    @Inject
    private FestivalDao festivalDao;

    @Inject
    private UserDao userDao;

    @Inject
    @Named(FESTIVAL_CACHE)
    private LoadingCache<Fid, CompletableFuture<Festival>> festivalCache;


    @Transactional(TRANSACTION_MANAGER)
    public CreatedFestival create(NewFestival newFestival) {
        val fid = festivalDao.create(newFestival);
        return CreatedFestival
                .builder()
                .fid(fid)
                .session(userDao.register(fid, UserType.Admin, UserState.Approved,
                        newFestival.getUserName(), newFestival.getUserKey()))
                .build() ;

    }

    @Transactional(TRANSACTION_MANAGER)
    @SneakyThrows
    public void setState(Fid fid, FestivalState state) {
        festivalCache.get(fid).setState(state);
        // send update to all
        festivalDao.setState(fid, state);
    }

    @Transactional(transactionManager = TRANSACTION_MANAGER, readOnly = true)
    public List<MenuItem> listMenu(Fid fid) {
        return festivalDao.getMenu(fid);
    }

    public void updateMenu(Fid fid, List<MenuItem> items) {
        festivalDao.setMenu(fid, items);
    }
}
