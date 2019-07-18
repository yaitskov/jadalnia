package org.dan.jadalnia.app.ws;

import com.google.common.collect.Multimap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.dan.jadalnia.app.user.Cid;
import org.dan.jadalnia.app.user.Uid;
import org.dan.jadalnia.app.user.UserWsListener;
import org.dan.jadalnia.app.user.customer.CustomerWsListener;

@Getter
@Builder
@FieldDefaults(makeFinal = true)
@AllArgsConstructor
public class FestivalListeners {
    Multimap<Cid, CustomerWsListener> customerListeners;
    Multimap<Uid, UserWsListener> userListeners;
}
