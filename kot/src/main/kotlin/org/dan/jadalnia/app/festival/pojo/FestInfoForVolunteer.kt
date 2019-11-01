package org.dan.jadalnia.app.festival.pojo

import java.time.Instant

data class FestInfoForVolunteer(
    val name: String,
    val principal: String,
    val opensAt: Instant,
    val state: FestivalState)
