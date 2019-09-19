package org.dan.jadalnia.app.order;

import lombok.SneakyThrows;
import lombok.val;
import org.dan.jadalnia.app.festival.menu.DishName;
import org.dan.jadalnia.app.festival.pojo.FestivalState;
import org.dan.jadalnia.app.festival.pojo.Fid;
import org.dan.jadalnia.app.order.pojo.OrderItem;
import org.dan.jadalnia.app.order.pojo.OrderLabel;
import org.dan.jadalnia.app.user.UserRegRequest;
import org.dan.jadalnia.app.user.UserResource;
import org.dan.jadalnia.app.user.UserSession;
import org.dan.jadalnia.app.user.UserType;
import org.dan.jadalnia.mock.MyRest;
import org.dan.jadalnia.test.ws.WsIntegrationTest;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.util.Collections.singletonList;
import static org.dan.jadalnia.app.festival.NewFestivalTest.createFestival;
import static org.dan.jadalnia.app.festival.NewFestivalTest.genAdminKey;
import static org.dan.jadalnia.app.festival.SetFestivalStateTest.setState;
import static org.junit.Assert.assertThat;

public class CustomerPutsOrderTest extends WsIntegrationTest {
    public static UserSession registerCustomer(Fid fid, String key, MyRest myRest) {
        return registerUser(fid, key, myRest, UserType.Customer);
    }

    public static UserSession registerUser(
            Fid fid, String key, MyRest myRest, UserType userType) {
        return myRest.anonymousPost(UserResource.REGISTER,
                new UserRegRequest(fid, "user" + key, key, userType),
                UserSession.class);
    }

    public static String genUserKey() {
        return UUID.randomUUID().toString();
    }

    public static OrderLabel putOrder(
            MyRest myRest, UserSession session,
            List<OrderItem> items) {
        return myRest.post(OrderResource.PUT_ORDER, session, items, OrderLabel.class);
    }

    @Test
    @SneakyThrows
    public void customerPutsOrder() {
        val key = genAdminKey();
        val festival = createFestival(key, myRest());
        val customerSession = registerCustomer(
                festival.getFid(), genUserKey(), myRest());
        setState(myRest(), festival.getSession(), FestivalState.Open);

        assertThat(
                putOrder(myRest(),
                        customerSession,
                        singletonList(
                                new OrderItem(
                                        new DishName("rzemniaki"),
                                        1,
                                        Collections.emptyList())))
                        .toString(),
                Matchers.matchesPattern("^[A-Z][0-9]+$"));
    }
}
