package org.dan.jadalnia.app.festival.pojo

import org.dan.jadalnia.app.festival.menu.MenuItem
import java.time.Instant

data class FestivalInfo(
        val fid: Fid,
        val name: String,
        val state: FestivalState,
        val menu: List<MenuItem>,
        val params: FestParams,
        val opensAt: Instant) {

    fun withState(state1: FestivalState) = copy(state = state1)
    fun withMenu(menu: List<MenuItem>) = copy(menu = menu)
}
