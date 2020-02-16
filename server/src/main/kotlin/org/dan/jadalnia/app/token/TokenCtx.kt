package org.dan.jadalnia.app.token

import org.dan.jadalnia.app.order.pref.CashierPerformanceService
import org.dan.jadalnia.app.order.pref.CashierPerformanceResource
import org.dan.jadalnia.app.order.stats.TokenStatsResource
import org.dan.jadalnia.app.order.stats.TokenStatsService
import org.springframework.context.annotation.Import

@Import(TokenDao::class,
    TokenStatsDao::class,
    TokenResource::class,
    CashierPerformacenDao::class,
    CashierPerformanceService::class,
    CashierPerformanceResource::class,
    TokenStatsResource::class,
    TokenStatsService::class,
    TokenBalanceCacheLoader::class,
    TokenBalanceCacheFactory::class,
    TokenService::class)
class TokenCtx