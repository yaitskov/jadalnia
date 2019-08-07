package org.dan.jadalnia.sys.async;

import javax.ws.rs.container.AsyncResponse;
import java.util.concurrent.CompletableFuture;

public class AsynSync {
    public <T> void sync(
            CompletableFuture<T> resultFuture,
            AsyncResponse response) {
        resultFuture.whenComplete(
                (result, e) -> {
                    if (e == null) {
                        response.resume(result);
                    } else {
                        response.resume(e);
                    }
                });
    }
}
