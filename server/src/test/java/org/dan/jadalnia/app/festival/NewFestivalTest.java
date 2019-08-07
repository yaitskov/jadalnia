package org.dan.jadalnia.app.festival;

import lombok.val;
import org.dan.jadalnia.app.festival.pojo.CreatedFestival;
import org.dan.jadalnia.app.festival.pojo.NewFestival;
import org.dan.jadalnia.mock.MyRest;
import org.dan.jadalnia.sys.ctx.TestCtx;
import org.dan.jadalnia.test.AbstractSpringJerseyTest;
import org.dan.jadalnia.test.JerseySpringTest;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.test.context.ContextConfiguration;

import java.time.Instant;
import java.util.UUID;

import static org.dan.jadalnia.app.festival.FestivalResource.FESTIVAL_CREATE;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@Category(JerseySpringTest.class)
@ContextConfiguration(classes = TestCtx.class)
public class NewFestivalTest extends AbstractSpringJerseyTest {
    public static CreatedFestival createFestival(String key, MyRest myRest) {
        return myRest.post(FESTIVAL_CREATE,
                NewFestival
                        .builder()
                        .opensAt(Instant.now())
                        .name(label("festival"))
                        .userName(label("user"))
                        .userKey(key)
                        .build()).readEntity(CreatedFestival.class);
    }

    @Test
    public void createNewFestival() {
        val key = UUID.randomUUID().toString();
        val result = createFestival(key, myRest());

        assertThat(result.getFid().intValue(), greaterThan(0));
        assertThat(result.getSession().getUid().intValue(), greaterThan(0));
        assertThat(result.getSession().getKey(), is(key));
    }

    @NotNull
    public static String label(String s) {
        return (s + "-" + UUID.randomUUID()).substring(0, 30);
    }
}
