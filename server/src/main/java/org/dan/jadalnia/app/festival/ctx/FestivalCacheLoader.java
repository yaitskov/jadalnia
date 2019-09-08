package org.dan.jadalnia.app.festival.ctx;

import com.google.common.cache.CacheLoader;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.dan.jadalnia.app.festival.FestivalDao;
import org.dan.jadalnia.app.festival.pojo.Festival;
import org.dan.jadalnia.app.festival.pojo.Fid;
import org.dan.jadalnia.app.label.LabelDao;
import org.dan.jadalnia.app.order.OrderAggregator;
import org.dan.jadalnia.app.order.OrderDao;
import org.dan.jadalnia.app.ws.WsBroadcast;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.concurrent.CompletableFuture.completedFuture;


@Slf4j
@FieldDefaults(makeFinal = true)
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class FestivalCacheLoader extends CacheLoader<Fid, CompletableFuture<Festival>>  {
    LabelDao labelDao;
    OrderDao orderDao;
    FestivalDao festivalDao;
    OrderAggregator orderAggregator;
    WsBroadcast wsBroadcast;

    @Override
    public CompletableFuture<Festival> load(Fid fid) {
        return festivalDao.getById(fid).thenCompose(festInfo -> {
            return labelDao.maxOrderNumber(fid)
                    .thenCompose(maxId -> {
                        return orderDao.loadPaid(fid).thenCompose(paidOrders -> {
                            return completedFuture(Festival
                                    .builder()
                                    .info(new AtomicReference<>(festInfo))
                                    .paidOrders(paidOrders)
                                    .nextLabel(new AtomicInteger(maxId.getId() + 1))
                                    .requiredItems(orderAggregator.aggregate(paidOrders.values()))
                                    .kelnersProcessingOrders(wsBroadcast.busyKelners(fid))
                                    .build());
                        });
                    });
        });
    }
}
