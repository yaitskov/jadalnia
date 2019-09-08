package org.dan.jadalnia.app.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.dan.jadalnia.app.festival.pojo.Fid;
import org.dan.jadalnia.app.order.pojo.Oid;
import org.dan.jadalnia.app.user.Uid;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class WsBroadcast {
    ObjectMapper objectMapper;
    Map<Fid, FestivalListeners> fid2Listeners;

    @SneakyThrows
    public void broadcast(Fid fid, MessageForClient message) {
        val listeners = getListeners(fid);
        val serializedMessage = objectMapper.writeValueAsBytes(message);

        broadcastTo(listeners.getCustomerListeners().values(), serializedMessage);
        broadcastTo(listeners.getUserListeners().values(), serializedMessage);
    }

    public FestivalListeners getListeners(Fid fid) {
        val listeners = fid2Listeners.get(fid);
        if (listeners == null) {
            fid2Listeners.putIfAbsent(
                    fid,
                    new FestivalListeners(
                            new ConcurrentHashMap<>(),
                            new ConcurrentHashMap<>()));
            return fid2Listeners.get(fid);
        }
        return listeners;
    }

    public Map<Oid, Uid> busyKelners(Fid fid) {
        return null;
    }

    public static void broadcastTo(
            Collection<? extends WsListener> listeners, byte[] message) {
        // close ws on failure
        listeners.forEach(listener -> listener.send(message));
    }
}
