package org.dan.jadalnia.app.ws

import org.dan.jadalnia.app.user.customer.CustomerWsListener
import org.springframework.context.annotation.Import

@Import(WsBroadcast::class,
        FestivalListenersProvider::class,
        CustomerWsListener::class)
class WsCtx
