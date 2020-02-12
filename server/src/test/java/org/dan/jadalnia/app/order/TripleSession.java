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

    String kelnerName;
    String cashierName;

    public static TripleSession triSession(Fid fid, MyRest myRest) {
        String kelnerKey = genUserKey();
        String cashierKey = genUserKey();
        return TripleSession.builder()
                .customer(registerCustomer(
                        fid, genUserKey(), myRest))
                .kelner(registerKelner(fid, kelnerKey, myRest))
                .cashier(registerKasier(fid, cashierKey, myRest))
                .cashierName("user" + cashierKey)
                .kelnerName("user" + kelnerKey)
        .build();
    }
}
