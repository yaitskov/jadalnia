package org.dan.jadalnia.app.festival;

import lombok.val;
import org.dan.jadalnia.app.festival.pojo.CreatedFestival;
import org.dan.jadalnia.app.festival.pojo.NewFestival;
import org.dan.jadalnia.mock.MyRest;
import org.dan.jadalnia.test.ws.WsIntegrationTest;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.time.Instant;
import java.util.UUID;

import static org.dan.jadalnia.app.festival.FestivalResource.FESTIVAL_CREATE;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class NewFestivalTest extends WsIntegrationTest {
    public static CreatedFestival createFestival(String key, MyRest myRest) {
        return myRest.anonymousPost(FESTIVAL_CREATE,
                new NewFestival(Instant.now(), label("festival"), label("user"), key),
                CreatedFestival.class);
    }

    @Test
    public void createNewFestival() {
        val key = genAdminKey();
        val result = createFestival(key, myRest());

        assertThat(result.getFid().intValue(), greaterThan(0));
        assertThat(result.getSession().getUid().intValue(), greaterThan(0));
        assertThat(result.getSession().getKey(), is(key));
    }

    @NotNull
    public static String genAdminKey() {
        return UUID.randomUUID().toString();
    }

    @NotNull
    public static String label(String s) {
        return (s + "-" + UUID.randomUUID()).substring(0, 30);
    }
}
