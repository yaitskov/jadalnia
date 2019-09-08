package org.dan.jadalnia.app.order;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.dan.jadalnia.app.festival.FestivalService;
import org.dan.jadalnia.app.festival.pojo.Festival;
import org.dan.jadalnia.app.label.LabelService;
import org.dan.jadalnia.app.order.pojo.OrderItem;
import org.dan.jadalnia.app.order.pojo.OrderLabel;
import org.dan.jadalnia.app.user.UserSession;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@FieldDefaults(makeFinal = true)
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class OrderService {
    FestivalService festivalService;
    OrderDao orderDao;
    LabelService labelService;

    public CompletableFuture<OrderLabel> putNewOrder(
            Festival festival,
            UserSession customerSession,
            List<OrderItem> newOrderItems) {
        return labelService
                .allocate(festival)
                .thenCompose(label -> orderDao.storeNewOrder(
                        festival.fid(),
                        customerSession.getUid(),
                        label,
                        newOrderItems));
    }
}
