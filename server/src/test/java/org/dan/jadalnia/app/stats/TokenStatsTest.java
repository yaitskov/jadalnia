package org.dan.jadalnia.app.stats;

import lombok.SneakyThrows;
import lombok.val;
import org.dan.jadalnia.app.festival.pojo.Fid;
import org.dan.jadalnia.app.order.MockBaseFestival;
import org.dan.jadalnia.app.token.TokenPoints;
import org.dan.jadalnia.app.token.TokenStats;
import org.dan.jadalnia.mock.MyRest;
import org.dan.jadalnia.test.ws.WsIntegrationTest;
import org.hamcrest.core.Is;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.dan.jadalnia.app.token.CustomerNotifiedAboutApprovedTokenTest.approveToken;
import static org.dan.jadalnia.app.token.CustomerNotifiedAboutApprovedTokenTest.requestToken;
import static org.junit.Assert.assertThat;

public class TokenStatsTest extends WsIntegrationTest {
    public static TokenStats tokenStats(MyRest myRest, Fid fid) {
        return myRest.get("/token-stats/" + fid, TokenStats.class);
    }

    @Test
    @SneakyThrows
    public void tokenStats() {
        val festState = MockBaseFestival.create(myRest());
        val customer = festState.getSessions().getCustomer();
        val rest = festState.getMyRest();

        val tokenId = requestToken(rest, TokenPoints.valueOf(10), customer);

        requestToken(rest, TokenPoints.valueOf(12), customer);

        approveToken(rest, customer.getUid(),
                asList(tokenId), festState.getSessions().getCashier());

        assertThat(
                tokenStats(rest, festState.getFestival().getFid()),
                Is.is(new TokenStats(
                        new TokenPoints(10),
                        new TokenPoints(0),
                        new TokenPoints(12),
                        new TokenPoints(0))));
    }
}
