package org.dan.jadalnia.app.push

data class PushSubscription(
    val pushUrl: String,
    val key: String,
    val auth: String)