package org.dan.jadalnia.app.festival;

import org.springframework.context.annotation.Import;

@Import({FestivalDao.class, FestivalResource.class,
        FestivalCacheFactory.class,
        FestivalCacheLoader.class,
        FestivalCache.class,
        FestivalService.class})
public class FestivalCtx {
}
