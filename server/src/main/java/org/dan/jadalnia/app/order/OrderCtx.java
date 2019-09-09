package org.dan.jadalnia.app.order;

import org.springframework.context.annotation.Import;

@Import({
        OrderDao.class,
        OrderResource.class,
        OrderService.class,
        OrderAggregator.class})
public class OrderCtx {
}