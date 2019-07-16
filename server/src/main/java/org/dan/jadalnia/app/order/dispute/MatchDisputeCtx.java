package org.dan.jadalnia.app.order.dispute;

import org.springframework.context.annotation.Import;

@Import({MatchDisputeResource.class, MatchDisputeService.class, MatchDisputeDaoServer.class})
public class MatchDisputeCtx {
}
