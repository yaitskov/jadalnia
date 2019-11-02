package org.dan.jadalnia.app.festival.pojo

import java.time.Instant

data class FestivalVolunteerInfo(
    val name: String,
    val state: FestivalState,
    val opensAt: Instant)