package org.dan.jadalnia.app.order

import org.springframework.context.annotation.Import

@Import(OrderDao::class,
    OrderResource::class,
    OrderCacheFactory::class,
    OrderCacheLoader::class,
    OrderService::class,
    CostEstimator::class,
    OrderAggregator::class)
class OrderCtx
