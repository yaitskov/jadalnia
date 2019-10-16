package org.dan.jadalnia.app.token;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.val;
import org.assertj.core.api.Condition;
import org.dan.jadalnia.app.festival.pojo.FestivalState;
import org.dan.jadalnia.app.user.Uid;
import org.dan.jadalnia.app.user.UserSession;
import org.dan.jadalnia.app.user.WsClientHandle;
import org.dan.jadalnia.app.ws.MessageForClient;
import org.dan.jadalnia.mock.MyRest;
import org.dan.jadalnia.test.match.PredicateStateMatcher;
import org.dan.jadalnia.test.ws.WsIntegrationTest;
import org.hamcrest.core.Is;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static java.util.Arrays.asList;

import static java.util.Collections.singletonMap;
import static org.dan.jadalnia.app.festival.NewFestivalTest.createFestival;
import static org.dan.jadalnia.app.festival.NewFestivalTest.genAdminKey;
import static org.dan.jadalnia.app.festival.SetFestivalStateTest.setState;
import static org.dan.jadalnia.app.order.KelnerNotifiedAboutPaidOrderTest.registerKasier;
import static org.dan.jadalnia.app.user.CustomerGetsFestivalStatusOnConnectTest.genUserKey;
import static org.dan.jadalnia.app.user.CustomerGetsFestivalStatusOnConnectTest.registerCustomer;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;


public class CustomerNotifiedAboutApprovedTokenTest extends WsIntegrationTest {
    public static TokenId requestToken(
            MyRest myRest, TokenPoints points, UserSession session) {
        return myRest.post(TokenResource.REQUEST_TOKEN,
                session, points, TokenId.class);
    }

    public static List<PreApproveTokenView> listPendingTokens(
            MyRest myRest, Uid customerUid, UserSession kasierSession) {
        return myRest.get(TokenResource.LIST_REQUESTS_FOR_APPROVE + "/" + customerUid,
                () -> kasierSession,
                new GenericType<List<PreApproveTokenView>>() {});
    }

    public static List<PreApproveTokenView> approveToken(
            MyRest myRest, Uid customerId, List<TokenId> tokenIds, UserSession session) {
        return myRest.post(TokenResource.APPROVE_REQUEST,
                Optional.of(session),
                new TokensApproveReq(customerId, tokenIds),
                new GenericType<List<PreApproveTokenView>>() {});
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
        val pendingTokenIds = listPendingTokens(myRest(), customerSession.getUid(), kasierSession);
        assertThat(pendingTokenIds, hasItem(hasProperty("tokenId", Is.is(tokenId))));
        val approvedToken = approveToken(
                myRest(), customerSession.getUid(), asList(tokenId), kasierSession);

        assertThat(approvedToken,
                hasItem(
                        allOf(
                                hasProperty("amount", Is.is(pointsInToken)),
                                hasProperty("tokenId", Is.is(tokenId)))));

        assertThat(
                listPendingTokens(myRest(), customerSession.getUid(), kasierSession),
                Is.is(empty()));
        wsCustomerHandler.waitTillMatcherSatisfied();
    }
}
