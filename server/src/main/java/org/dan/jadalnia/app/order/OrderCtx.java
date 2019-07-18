package org.dan.jadalnia.app.order;

import org.dan.jadalnia.app.order.dispute.MatchDisputeCtx;
import org.dan.jadalnia.app.order.rule.service.GroupOrderRuleServiceCtx;
import org.dan.jadalnia.app.playoff.PlayOffRuleValidator;
import org.dan.jadalnia.app.playoff.PlayOffService;
import org.springframework.context.annotation.Import;

@Import({OrderDao.class, OrderResource.class, OrderService.class,
        MatchRemover.class,
        MatchDisputeCtx.class, MatchEditorService.class,
        PlayOffService.class, PlayOffRuleValidator.class,
        AffectedMatchesService.class,
        AffectedConsoleMatchesService.class,
        GroupOrderRuleServiceCtx.class})
public class OrderCtx {
}
