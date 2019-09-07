package org.dan.jadalnia.test.ws;

import java.util.Collections;
import java.util.Map;

public interface WsHandler {
    default Map<String, String> getHeaders() {
        return Collections.emptyMap();
    }
}
