package org.dan.jadalnia.app.order;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.dan.jadalnia.app.festival.pojo.Fid;
import org.dan.jadalnia.app.user.UserSession;
import org.dan.jadalnia.mock.MyRest;

import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.registerKasier;
import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.registerKelner;
import static org.dan.jadalnia.app.user.CustomerGetsFestivalStatusOnConnectTest.genUserKey;
import static org.dan.jadalnia.app.user.CustomerGetsFestivalStatusOnConnectTest.registerCustomer;

@Getter
@Builder
@FieldDefaults(makeFinal = true)
public class TripleSession {
    UserSession customer;
    UserSession kelner;
    UserSession cashier;

    public static TripleSession triSession(Fid fid, MyRest myRest) {
        return TripleSession.builder()
                .customer(registerCustomer(
                        fid, genUserKey(), myRest))
                .kelner(registerKelner(fid, genUserKey(), myRest))
                .cashier(registerKasier(fid, genUserKey(), myRest))
        .build();
    }
}
