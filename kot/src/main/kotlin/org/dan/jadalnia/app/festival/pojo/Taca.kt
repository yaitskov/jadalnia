package org.dan.jadalnia.app.festival.pojo

import org.dan.jadalnia.app.order.pojo.OrderLabel
import java.time.Instant

data class Taca(val label: OrderLabel, val paidAt: Instant)