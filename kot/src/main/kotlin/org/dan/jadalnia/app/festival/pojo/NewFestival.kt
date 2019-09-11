package org.dan.jadalnia.app.festival.pojo;

import java.time.Instant;

data class NewFestival(
    val opensAt: Instant,
    val name: String,
    val userName: String,
    val userKey: String)
