package org.dan.jadalnia.app.push

import nl.martijndwars.webpush.Encoding
import nl.martijndwars.webpush.Notification
import nl.martijndwars.webpush.PushService
import org.apache.http.HttpResponse
import org.apache.http.concurrent.FutureCallback
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient
import org.dan.jadalnia.sys.error.JadEx.Companion.internalError
import java.util.concurrent.CompletableFuture
import javax.inject.Inject

class AsyncPushServiceDecorator @Inject constructor (
    val httpClient: CloseableHttpAsyncClient,
    val preparator: PushService) {
  fun sendNio(notification: Notification): CompletableFuture<HttpResponse> {
    val httpPost = preparator.preparePost(notification, Encoding.AES128GCM)

    val result: CompletableFuture<HttpResponse> = CompletableFuture();

    httpClient.execute(httpPost, object : FutureCallback<HttpResponse> {
      override fun cancelled() =
        failed(internalError("delivery of push is cancelled",
            "payload", String(notification.payload)))

      override fun completed(response: HttpResponse) {
        result.complete(response)
      }

      override fun failed(ex: Exception?) {
        result.completeExceptionally(ex)
      }
    })
    return result;
  }
}