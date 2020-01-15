package org.dan.jadalnia.app.order

import org.dan.jadalnia.app.order.complete.CustomerAbsent
import org.dan.jadalnia.app.order.complete.OrderReady
import org.dan.jadalnia.app.user.WithUser
import org.springframework.context.annotation.Import

@Import(OrderDao::class,
    OrderResource::class,
    OrderCacheFactory::class,
    OrderCacheLoader::class,
    OrderService::class,
    CostEstimator::class,
    OrderReady::class,
    WithUser::class,
    CustomerAbsent::class,
    OrderAggregator::class)
class OrderCtx
