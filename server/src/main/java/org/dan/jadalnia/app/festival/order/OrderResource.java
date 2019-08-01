package org.dan.jadalnia.app.festival.order;

import lombok.extern.slf4j.Slf4j;
import org.dan.jadalnia.app.auth.AuthService;
import org.dan.jadalnia.util.time.Clocker;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Slf4j
@Path("/")
@Produces(APPLICATION_JSON)
public class OrderResource {
    public static final String MATCH_RESULT = "/match/result/";
    public static final String MATCH_LIST_PLAYED_ME = "/match/list/played-by-me/";
    public static final String MATCH_LIST_JUDGED = "/match/list/judged/";
    public static final String MY_PENDING_MATCHES = "/match/list/my/pending/";
    public static final String BID_PENDING_MATCHES = "/match/list/bid/pending/";
    public static final String OPEN_MATCHES_FOR_JUDGE = "/match/judge/list/open/";
    public static final String SCORE_SET = "/match/participant/score";
    public static final String MATCH_WATCH_LIST_OPEN = "/match/watch/list/open/";
    public static final String MATCH_RESET_SET_SCORE = "/match/reset-set-score";
    public static final String TID_JP = "{tid}";
    public static final String BID_JP = "{bid}";
    public static final String UID = "uid";
    private static final String MATCH_RULES = "/match/rules/";
    private static final String MID = "mid";
    private static final String MID_JP = "{mid}";
    public static final String MATCH_FOR_JUDGE = "/match/for-judge/";
    public static final String RESCORE_MATCH = "/match/rescore-match";
    public static final String MATCH_FIND_BY_PARTICIPANTS = "/match/find-by-participants/";

    @Inject
    private AuthService authService;

    @Inject
    private OrderService orderService;


    @Inject
    private Clocker clocker;
}
