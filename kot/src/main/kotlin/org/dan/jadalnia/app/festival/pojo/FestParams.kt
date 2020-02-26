package org.dan.jadalnia.app.festival.pojo

data class FestParams(
    val freeKelnerActiveWithInMs: Int = 10_000,
    val defaultOrderKeepMs: Int = 50_000,
    val defaultAvgMealMs: Int = 60_000,
    val manualAdjustPerOrderMs: Int = 0,
    val guestInfo: String = "")
