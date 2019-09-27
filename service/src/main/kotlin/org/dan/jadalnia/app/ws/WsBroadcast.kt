package org.dan.jadalnia.app.ws

import com.fasterxml.jackson.databind.ObjectMapper
import org.dan.jadalnia.app.festival.pojo.Festival

import org.dan.jadalnia.app.festival.pojo.Fid
import org.dan.jadalnia.app.user.Uid
import org.slf4j.LoggerFactory
import java.util.*

import javax.inject.Inject
import java.util.concurrent.ConcurrentHashMap
import java.util.stream.Collectors.toList

class WsBroadcast @Inject constructor(
        val objectMapper: ObjectMapper,
        val fid2Listeners: MutableMap<Fid, FestivalListeners>) {

    fun broadcast(fid: Fid, message: MessageForClient) {
        val listeners = getListeners(fid)
        val serializedMessage: ByteArray = objectMapper.writeValueAsBytes(message)

        broadcastTo(listeners.customerListeners.values, serializedMessage)
        broadcastTo(listeners.volunteerListeners.values, serializedMessage)
    }

    fun notifyCustomers(fid: Fid, customers: Collection<Uid>, message: MessageForClient) {
        val listeners = getListeners(fid)
        val serializedMessage: ByteArray = objectMapper.writeValueAsBytes(message)
        log.info("notify customers {} of festival {} with {}", customers, fid, message)
        broadcastTo(
            customers.stream()
                .map { uid -> Optional.ofNullable(listeners.customerListeners[uid]) }
                .filter { o -> o.isPresent() }
                .map { o -> o.get() }
                .collect(toList()),
        serializedMessage)
    }

    fun broadcastTo(listeners: Collection<WsListener>, message: MessageForClient) {
        val serializedMessage: ByteArray = objectMapper.writeValueAsBytes(message)
        broadcastTo(listeners, serializedMessage)
    }

    fun getListeners(fid: Fid): FestivalListeners {
        val listeners = fid2Listeners[fid]
        if (listeners == null) {
            val festivalListeners = FestivalListeners(
                    customerListeners = ConcurrentHashMap(),
                    volunteerListeners = ConcurrentHashMap(),
                    kelnerUids = ConcurrentHashMap(),
                    kasierUids = ConcurrentHashMap(),
                    adminUids = ConcurrentHashMap(),
                    cookUids = ConcurrentHashMap())
            return fid2Listeners.putIfAbsent(fid, festivalListeners)
                    ?: return festivalListeners
        }
        return listeners
    }

    fun broadcastToFreeKelners(festival: Festival, message: MessageForClient) {
        broadcastTo(
                getListeners(festival.fid())
                        .volunteerListeners
                        .filterKeys { uid -> festival.freeKelners.containsKey(uid) }.values,
                message)
    }

    companion object {
        fun broadcastTo(
                listeners: Collection<WsListener>,
                message: ByteArray) {
            // close ws on failure
            listeners.forEach { listener -> listener.send(message) }
        }

        val log = LoggerFactory.getLogger(WsBroadcast::class.java)
    }
}
