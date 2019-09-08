package org.dan.jadalnia.app.label;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.dan.jadalnia.app.festival.pojo.Festival;
import org.dan.jadalnia.app.order.pojo.OrderLabel;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

@Slf4j
@FieldDefaults(makeFinal = true)
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class LabelService {
    LabelDao labelDao;

    public CompletableFuture<OrderLabel> allocate(Festival festival) {
        return labelDao.storeNew(
                festival.fid(),
                festival.getNextLabel().getAndIncrement());
    }
}
