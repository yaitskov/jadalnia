package org.dan.jadalnia.app.ws;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.dan.jadalnia.app.user.Cid;
import org.dan.jadalnia.app.user.ParticipantWsListener;
import org.dan.jadalnia.app.user.Uid;
import org.dan.jadalnia.app.user.customer.CustomerWsListener;

import java.util.Map;

@Getter
@Builder
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class FestivalListeners {
    Map<Cid, CustomerWsListener> customerListeners;
    Map<Uid, ParticipantWsListener> userListeners;
}
