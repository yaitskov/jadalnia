package org.dan.jadalnia.app.order

import org.dan.jadalnia.app.order.complete.CustomerAbsent
import org.dan.jadalnia.app.order.complete.KelnerResigns
import org.dan.jadalnia.app.order.complete.LowFood
import org.dan.jadalnia.app.order.complete.OrderReady
import org.dan.jadalnia.app.order.pref.KelnerPerfomanceService
import org.dan.jadalnia.app.order.pref.KelnerPerformanceResource
import org.dan.jadalnia.app.order.stats.OrderStatsResource
import org.dan.jadalnia.app.order.stats.OrderStatsService
import org.dan.jadalnia.app.user.WithUser
import org.springframework.context.annotation.Import

@Import(OrderDao::class,
    DelayedOrderDao::class,
    OrderResource::class,
    OrderCacheFactory::class,
    OrderCacheLoader::class,
    OrderService::class,
    CostEstimator::class,
    OrderReady::class,
    KelnerResigns::class,
    LowFood::class,
    OrderStatsDao::class,
    OrderStatsService::class,
    OrderStatsResource::class,
    WithUser::class,
    KelnerPerformanceResource::class,
    KelnerPerfomanceService::class,
    KelnerPerformacenDao::class,
    CustomerAbsent::class,
    OrderAggregator::class)
class OrderCtx
