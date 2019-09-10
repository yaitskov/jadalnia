package org.dan.jadalnia.app.festival;

import lombok.val;
import org.dan.jadalnia.app.festival.menu.DishName;
import org.dan.jadalnia.app.festival.menu.MenuItem;
import org.dan.jadalnia.app.festival.pojo.Fid;
import org.dan.jadalnia.app.user.UserSession;
import org.dan.jadalnia.mock.MyRest;
import org.dan.jadalnia.sys.ctx.TestCtx;
import org.dan.jadalnia.test.AbstractSpringJerseyTest;
import org.dan.jadalnia.test.JerseySpringTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.test.context.ContextConfiguration;

import javax.ws.rs.core.GenericType;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.dan.jadalnia.app.festival.FestivalResource.FESTIVAL_MENU;
import static org.dan.jadalnia.app.festival.NewFestivalTest.createFestival;
import static org.dan.jadalnia.app.festival.NewFestivalTest.genAdminKey;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@Category(JerseySpringTest.class)
@ContextConfiguration(classes = TestCtx.class)
public class SetMenuTest extends AbstractSpringJerseyTest {
    static final DishName FRYTKI = new DishName("Frytki");

    public static int setMenu(MyRest myRest, UserSession session) {
        return myRest.post(FESTIVAL_MENU,
                session,
                singletonList(
                        new MenuItem(
                                FRYTKI,
                                "fried potatoes",
                                3.14,
                                false,
                                singletonList(new MenuItem(
                                        new DishName("sos ostry"),
                                        null,
                                        0.0,
                                        false,
                                        null)
                                ))),
                Integer.class);
    }

    public static List<MenuItem> getMenu(MyRest myRest, Fid fid) {
        return myRest.get(FESTIVAL_MENU + "/" + fid, new GenericType<List<MenuItem>>(){});
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
