package org.dan.jadalnia.app.ws;

import org.dan.jadalnia.app.user.VolunteerWsListener
import org.dan.jadalnia.app.user.Uid
import org.dan.jadalnia.app.user.UserType
import org.dan.jadalnia.app.user.customer.CustomerWsListener
import org.dan.jadalnia.sys.error.JadEx.Companion.internalError
import java.util.concurrent.ConcurrentHashMap


class FestivalListeners (
        val customerListeners: MutableMap<Uid, CustomerWsListener>,
        val volunteerListeners: MutableMap<Uid, VolunteerWsListener>,

        val kelnerUids: ConcurrentHashMap<Uid, Unit>,
        val kasierUids: ConcurrentHashMap<Uid, Unit>,
        val adminUids: ConcurrentHashMap<Uid, Unit>,
        val cookUids: ConcurrentHashMap<Uid, Unit>) {

    fun addUid(uid: Uid, userType: UserType) {
        when (userType) {
            UserType.Cook -> cookUids[uid] = Unit
            UserType.Admin -> adminUids[uid] = Unit
            UserType.Kelner -> kelnerUids[uid] = Unit
            UserType.Kasier -> kasierUids[uid] = Unit
            else -> throw internalError("not supported type", "type", userType)
        }
    }

    fun removeUid(uid: Uid, userType: UserType) {
        when (userType) {
            UserType.Cook -> cookUids.remove(uid, Unit)
            UserType.Admin -> adminUids.remove(uid, Unit)
            UserType.Kelner -> kelnerUids.remove(uid, Unit)
            UserType.Kasier -> kasierUids.remove(uid, Unit)
            else -> throw internalError("not supported type", "type", userType)
        }
    }
}

