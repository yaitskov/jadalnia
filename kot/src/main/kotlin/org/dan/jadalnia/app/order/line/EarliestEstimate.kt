package org.dan.jadalnia.app.order.line

import org.dan.jadalnia.app.order.pojo.OrderLabel
import java.util.concurrent.CompletableFuture


data class EarliestEstimate(
    val queuePosition: Int,
    val estimateMinutes: CompletableFuture<Int>,
    val firstInLine: OrderLabel)
