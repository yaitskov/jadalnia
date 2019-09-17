package org.dan.jadalnia.app.ws;

import org.dan.jadalnia.app.user.ParticipantWsListener
import org.dan.jadalnia.app.user.Uid
import org.dan.jadalnia.app.user.customer.CustomerWsListener


class FestivalListeners (
        val customerListeners: MutableMap<Uid, CustomerWsListener>,
        val userListeners: Map<Uid, ParticipantWsListener>)

