package org.dan.jadalnia.app.festival.menu;

data class MenuItem(
    val name: DishName,
    val description: String?,
    val price: Double,
    val disabled: Boolean,
    val additions: List<MenuItem>)