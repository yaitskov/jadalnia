package org.dan.jadalnia.app.festival.ctx;

import org.dan.jadalnia.app.festival.FestivalDao;
import org.dan.jadalnia.app.festival.FestivalResource;
import org.dan.jadalnia.app.festival.FestivalService;
import org.springframework.context.annotation.Import;

@Import({FestivalDao.class, FestivalResource.class,
        FestivalCacheFactory.class,
        FestivalCacheLoader.class,
        FestivalService.class})
public class FestivalCtx {
}
