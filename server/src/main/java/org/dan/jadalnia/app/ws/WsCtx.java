package org.dan.jadalnia.app.ws;

import org.springframework.context.annotation.Import;

@Import({WsBroadcast.class, FestivalListenersProvider.class})
public class WsCtx {

}
