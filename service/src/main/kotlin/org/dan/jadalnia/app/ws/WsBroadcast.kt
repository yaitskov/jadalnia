package org.dan.jadalnia.app.ws

import com.fasterxml.jackson.databind.ObjectMapper

import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.order.pojo.Oid
import org.dan.jadalnia.app.user.Uid

import javax.inject.Inject
import java.util.concurrent.ConcurrentHashMap
//import java.util.Map

class WsBroadcast @Inject constructor(
        val objectMapper: ObjectMapper,
        val fid2Listeners: MutableMap<Fid, FestivalListeners>) {

    fun broadcast(fid: Fid, message: MessageForClient) {
        val listeners = getListeners(fid);
        val serializedMessage: ByteArray = objectMapper.writeValueAsBytes(message);

        broadcastTo(listeners.customerListeners.values, serializedMessage);
        broadcastTo(listeners.userListeners.values, serializedMessage);
    }

    fun getListeners(fid: Fid): FestivalListeners {
        val listeners = fid2Listeners[fid];
        if (listeners == null) {
            val festivalListeners = FestivalListeners(
                    ConcurrentHashMap(), ConcurrentHashMap())
            return fid2Listeners.putIfAbsent(fid, festivalListeners)
                    ?: return festivalListeners
        }
        return listeners
    }

    fun busyKelners(fid: Fid) = HashMap<Oid, Uid>()

    companion object {
        fun broadcastTo(
                listeners: Collection<WsListener>,
                message: ByteArray) {
            // close ws on failure
            listeners.forEach({
                listener -> listener.send(message)
            })
        }
    }
}
