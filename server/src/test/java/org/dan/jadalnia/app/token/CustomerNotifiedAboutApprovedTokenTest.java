package org.dan.jadalnia.app.token;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.val;
import org.dan.jadalnia.app.festival.pojo.FestivalState;
import org.dan.jadalnia.app.user.Uid;
import org.dan.jadalnia.app.user.UserSession;
import org.dan.jadalnia.app.user.WsClientHandle;
import org.dan.jadalnia.app.ws.MessageForClient;
import org.dan.jadalnia.mock.MyRest;
import org.dan.jadalnia.test.match.PredicateStateMatcher;
import org.dan.jadalnia.test.ws.WsIntegrationTest;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.dan.jadalnia.app.festival.NewFestivalTest.createFestival;
import static org.dan.jadalnia.app.festival.NewFestivalTest.genAdminKey;
import static org.dan.jadalnia.app.festival.SetFestivalStateTest.setState;
import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.registerKasier;
import static org.dan.jadalnia.app.user.CustomerGetsFestivalStatusOnConnectTest.genUserKey;
import static org.dan.jadalnia.app.user.CustomerGetsFestivalStatusOnConnectTest.registerCustomer;


public class CustomerNotifiedAboutApprovedTokenTest extends WsIntegrationTest {
    public static TokenId requestToken(
            MyRest myRest, TokenPoints points, UserSession session) {
        return myRest.post(TokenResource.REQUEST_TOKEN,
                session, points, TokenId.class);
    }

    public static TokenId approveToken(
            MyRest myRest, Uid customerId, TokenPoints points, UserSession session) {
        return myRest.post(TokenResource.APPROVE_REQUEST,
                session, new TokenApproveReq(customerId, points), TokenId.class);
    }

    @Test
    public void customerRequestsTokenAndKasierApprovesIt() {
        val festival = createFestival(genAdminKey(), myRest());
        val customerSession = registerCustomer(
                festival.getFid(), genUserKey(), myRest());
        val kasierSession = registerKasier(
                festival.getFid(), genUserKey(), myRest());

        setState(myRest(), festival.getSession(), FestivalState.Open);

        val pointsInToken = TokenPoints.valueOf(20);
        val tokenId = requestToken(myRest(), pointsInToken, customerSession);

        val wsCustomerHandler = WsClientHandle.wsClientHandle(
                customerSession,
                new PredicateStateMatcher<>(
                        (MessageForClient event) ->
                                event instanceof TokenApprovedEvent
                                        && ((TokenApprovedEvent) event).getTokenId().equals(tokenId),
                        new CompletableFuture<>()),
                new TypeReference<MessageForClient>() {
                });

        bindCustomerWsHandler(wsCustomerHandler);

        val approvedTokenId = approveToken(myRest(), customerSession.getUid(), pointsInToken, kasierSession);

        assertThat(approvedTokenId).isEqualTo(tokenId);

        wsCustomerHandler.waitTillMatcherSatisfied();
    }
}
