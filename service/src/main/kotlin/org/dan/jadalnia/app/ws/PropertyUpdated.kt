package org.dan.jadalnia.app.ws


data class PropertyUpdated<T>(val name: String, val newValue: T)
    : MessageForClient

