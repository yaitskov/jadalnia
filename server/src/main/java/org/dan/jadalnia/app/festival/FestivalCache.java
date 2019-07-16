package org.dan.jadalnia.app.festival;

import com.google.common.cache.LoadingCache;
import lombok.SneakyThrows;

import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.inject.Named;

public class FestivalCache implements Cache<Fid, Festival> {
    public static final String FESTIVAL_CACHE = "festival-cache";

    @Inject
    @Named(FESTIVAL_CACHE)
    private LoadingCache<Fid, Festival> festivalCache;

    @SneakyThrows
    public Festival load(Fid fid) {
        try {
            return festivalCache.get(fid);
        } catch (ExecutionException e) {
            throw e.getCause();
        }
    }

    public void invalidate(Fid fid) {
        festivalCache.invalidate(fid);
    }
}
