package org.dan.jadalnia.app.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.dan.jadalnia.app.festival.pojo.Festival;
import org.dan.jadalnia.app.festival.pojo.Fid;
import org.dan.jadalnia.app.festival.order.pojo.Oid;
import org.dan.jadalnia.app.user.Uid;

import javax.inject.Inject;
import java.util.Map;

@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class WsBroadcast {
    ObjectMapper objectMapper;

    public void broadcast(Festival festival, MessageForClient message) {
        //festival.getUserListeners().values().forEach(listener -> listener.send(message));
        //festival.getCustomerListeners().values().forEach(listener -> listener.send(message));
    }

    public Map<Oid, Uid> busyKelners(Fid fid) {
        return null;
    }
}
