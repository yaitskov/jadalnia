package org.dan.jadalnia.app.user;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.dan.jadalnia.app.festival.pojo.Fid;
import org.dan.jadalnia.mock.MyRest;
import org.dan.jadalnia.test.ws.WsIntegrationTest;
import org.hamcrest.core.Is;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.dan.jadalnia.app.festival.NewFestivalTest.createFestival;
import static org.dan.jadalnia.app.festival.NewFestivalTest.genAdminKey;
import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.registerKelner;
import static org.dan.jadalnia.app.user.CustomerGetsFestivalStatusOnConnectTest.genUserKey;
import static org.junit.Assert.assertThat;

@Slf4j
public class ListKelnerByTypeTest extends WsIntegrationTest {
    public static List<UserLink> listUsersByType(
            Fid fid, MyRest myRest, UserType userType) {
        return myRest.get(UserResource.USER + "list/" + fid + "/type/" + userType,
                new GenericType<List<UserLink>>() {});
    }

    @Test
    @SneakyThrows
    public void createKelnerCheckHimOnList() {
        val key = genAdminKey();
        val festival = createFestival(key, myRest());
        val kelnerSession = registerKelner(festival.getFid(), genUserKey(), myRest());

        assertThat(
                listUsersByType(festival.getFid(), myRest(), UserType.Kelner),
                Is.is(singletonList(new UserLink(kelnerSession.getUid(), "user" + kelnerSession.getKey()))));
        assertThat(
                listUsersByType(festival.getFid(), myRest(), UserType.Kasier),
                Is.is(emptyList()));
    }
}
