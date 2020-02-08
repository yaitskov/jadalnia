package org.dan.jadalnia.app.festival;

import lombok.val;
import org.dan.jadalnia.app.festival.menu.DishName;
import org.dan.jadalnia.app.festival.menu.MenuItem;
import org.dan.jadalnia.app.festival.pojo.Fid;
import org.dan.jadalnia.app.user.UserSession;
import org.dan.jadalnia.mock.MyRest;
import org.dan.jadalnia.test.ws.WsIntegrationTest;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.dan.jadalnia.app.festival.MenItemUtil.ofDish;
import static org.dan.jadalnia.app.festival.NewFestivalTest.createFestival;
import static org.dan.jadalnia.app.festival.NewFestivalTest.genAdminKey;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SetMenuTest extends WsIntegrationTest {
    public static final DishName FRYTKI = new DishName("Frytki");
    public static final DishName SUSZY = new DishName("Suszy");

    public static final List<MenuItem> FRYTKI_MENU = singletonList(
            new MenuItem(
                    FRYTKI,
                    "fried potatoes",
                    3.14,
                    false,
                    singletonList(ofDish("sos ostry"))));

    public static final List<MenuItem> FRYTKI_SUSZY_MENU = asList(
            new MenuItem(
                    SUSZY,
                    "ryba a ryrz",
                    2,
                    false,
                    singletonList(ofDish("musztarda"))),
            new MenuItem(
                    FRYTKI,
                    "fried potatoes",
                    3.14,
                    false,
                    singletonList(ofDish("sos ostry"))));

    public static int setMenu(MyRest myRest, UserSession session, List<MenuItem> items) {
        return myRest.post(FestivalResource.FESTIVAL_MENU, session, items, Integer.class);
    }

    public static int setMenu(MyRest myRest, UserSession session) {
        return setMenu(myRest, session, FRYTKI_MENU);
    }

    public static List<MenuItem> getMenu(MyRest myRest, Fid fid) {
        return myRest.get(FestivalResource.FESTIVAL_MENU + "/" + fid, new GenericType<List<MenuItem>>(){});
    }

    @Test
    public void setMenu() {
        val key = genAdminKey();
        val result = createFestival(key, myRest());
        val updatedRows = setMenu(myRest(), result.getSession());

        assertThat(updatedRows, is(1));
        assertThat(
                getMenu(myRest(), result.getFid()),
                hasItem(hasProperty("name", is(FRYTKI))));
    }
}
