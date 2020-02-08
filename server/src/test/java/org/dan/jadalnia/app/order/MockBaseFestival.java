package org.dan.jadalnia.app.order;

import lombok.Builder;
import lombok.Getter;
import lombok.val;
import org.dan.jadalnia.app.festival.menu.MenuItem;
import org.dan.jadalnia.app.festival.pojo.CreatedFestival;
import org.dan.jadalnia.app.festival.pojo.FestivalState;
import org.dan.jadalnia.mock.MyRest;

import java.util.List;

import static org.dan.jadalnia.app.festival.NewFestivalTest.createFestival;
import static org.dan.jadalnia.app.festival.NewFestivalTest.genAdminKey;
import static org.dan.jadalnia.app.festival.SetFestivalStateTest.setState;
import static org.dan.jadalnia.app.festival.SetMenuTest.FRYTKI_MENU;
import static org.dan.jadalnia.app.festival.SetMenuTest.setMenu;
import static org.dan.jadalnia.app.order.TripleSession.triSession;

@Getter
@Builder
public class MockBaseFestival {
    String adminKey;
    CreatedFestival festival;
    TripleSession sessions;
    MyRest myRest;

    public static MockBaseFestival create(MyRest myRest) {
        return create(myRest, FRYTKI_MENU);
    }

    public static MockBaseFestival create(MyRest myRest, List<MenuItem> menuItems) {
        val key = genAdminKey();
        val festival = createFestival(key, myRest);
        val sessions = triSession(festival.getFid(), myRest);

        setState(myRest, festival.getSession(), FestivalState.Open);
        setMenu(myRest, festival.getSession(), menuItems);

        return MockBaseFestival.builder()
                .myRest(myRest)
                .sessions(sessions)
                .festival(festival)
                .adminKey(key)
                .build();
    }
}
