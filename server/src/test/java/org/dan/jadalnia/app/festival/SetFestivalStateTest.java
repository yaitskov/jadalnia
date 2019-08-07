package org.dan.jadalnia.app.festival;

import lombok.val;
import org.dan.jadalnia.app.festival.pojo.FestivalState;
import org.dan.jadalnia.app.festival.pojo.Fid;
import org.dan.jadalnia.app.user.UserSession;
import org.dan.jadalnia.mock.MyRest;
import org.dan.jadalnia.sys.ctx.TestCtx;
import org.dan.jadalnia.test.AbstractSpringJerseyTest;
import org.dan.jadalnia.test.JerseySpringTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.test.context.ContextConfiguration;

import static org.dan.jadalnia.app.festival.FestivalResource.FESTIVAL_STATE;
import static org.dan.jadalnia.app.festival.NewFestivalTest.createFestival;
import static org.dan.jadalnia.app.festival.NewFestivalTest.genAdminKey;
import static org.dan.jadalnia.app.festival.pojo.FestivalState.Announce;
import static org.dan.jadalnia.app.festival.pojo.FestivalState.Close;
import static org.dan.jadalnia.app.festival.pojo.FestivalState.Open;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@Category(JerseySpringTest.class)
@ContextConfiguration(classes = TestCtx.class)
public class SetFestivalStateTest extends AbstractSpringJerseyTest {
    public static void setState(MyRest myRest, UserSession session, FestivalState state) {
        myRest.post(FESTIVAL_STATE, session, state);
    }

    public static FestivalState getState(MyRest myRest, Fid fid) {
        return myRest.get(FESTIVAL_STATE + "/" + fid, FestivalState.class);
    }

    @Test
    public void startServing() {
        val key = genAdminKey();
        val festival = createFestival(key, myRest());

        assertThat(getState(myRest(), festival.getFid()), is(Announce));

        setState(myRest(), festival.getSession(), Open);
        assertThat(getState(myRest(), festival.getFid()), is(Open));

        setState(myRest(), festival.getSession(), Close);
        assertThat(getState(myRest(), festival.getFid()), is(Close));
    }
}
