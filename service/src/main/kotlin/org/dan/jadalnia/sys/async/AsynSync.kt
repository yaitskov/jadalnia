package org.dan.jadalnia.sys.async

import javax.ws.rs.container.AsyncResponse
import java.util.concurrent.CompletableFuture

class AsynSync {
    fun <T> sync(
            resultFuture: CompletableFuture<T>,
            response: AsyncResponse ) {
        resultFuture.whenComplete(
                { result, e ->
                    if (e == null) {
                        response.resume(result)
                    } else {
                        response.resume(e)
                    }
                })
    }
}
