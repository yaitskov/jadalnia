package org.dan.jadalnia.app.festival;

import lombok.val;
import org.dan.jadalnia.app.festival.menu.DishName;
import org.dan.jadalnia.app.festival.menu.MenuItem;
import org.dan.jadalnia.app.user.UserSession;
import org.dan.jadalnia.mock.MyRest;
import org.dan.jadalnia.sys.ctx.TestCtx;
import org.dan.jadalnia.test.AbstractSpringJerseyTest;
import org.dan.jadalnia.test.JerseySpringTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.test.context.ContextConfiguration;

import java.util.UUID;

import static java.util.Collections.singletonList;
import static org.dan.jadalnia.app.festival.FestivalResource.FESTIVAL_MENU;
import static org.dan.jadalnia.app.festival.NewFestivalTest.createFestival;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@Category(JerseySpringTest.class)
@ContextConfiguration(classes = TestCtx.class)
public class SetMenuTest extends AbstractSpringJerseyTest {

    public static int setMenu(MyRest myRest, UserSession session) {
        return myRest.post(FESTIVAL_MENU, session,
                singletonList(
                        MenuItem
                                .builder()
                                .price(3.14)
                                .name(new DishName("Frytki"))
                                .additions(singletonList(MenuItem
                                        .builder()
                                        .name(new DishName("sos ostry"))
                                        .build()))
                                .description("fried potatoes")
                                .build()))
                        .readEntity(Integer.class);
    }

    @Test
    public void setMenu() {
        val key = UUID.randomUUID().toString();
        val result = createFestival(key, myRest());
        val updatedRows = setMenu(myRest(), result.getSession());

        assertThat(updatedRows, is(1));
    }
}
