package org.dan.jadalnia.app.festival.order;

import org.springframework.context.annotation.Import;

@Import({OrderDao.class, OrderResource.class, OrderService.class})
public class OrderCtx {
}
