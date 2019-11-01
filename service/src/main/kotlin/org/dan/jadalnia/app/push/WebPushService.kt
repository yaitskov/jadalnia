package org.dan.jadalnia.app.push

import com.fasterxml.jackson.databind.ObjectMapper
import nl.martijndwars.webpush.Notification
import org.dan.jadalnia.app.user.Uid
import org.dan.jadalnia.app.user.UserInfo
import org.dan.jadalnia.sys.error.JadEx.Companion.notFound
import org.dan.jadalnia.util.time.Clocker
import org.slf4j.LoggerFactory
import java.time.temporal.ChronoUnit
import java.util.concurrent.CompletableFuture
import javax.inject.Inject

class WebPushService @Inject constructor(
    val clock: Clocker,
    val objectMapper: ObjectMapper,
    val pushService: AsyncPushServiceDecorator,
    val webPushDao: WebPushDao) {
  companion object {
    val log = LoggerFactory.getLogger(WebPushService::class.java)
  }

  fun registerPushTopic(user: UserInfo, regReq: PushSubscription): CompletableFuture<Void> {
    log.info("Bind user {} to push topic {}", user.uid, regReq.pushUrl)
    return webPushDao.register(user.uid, regReq, clock.get().plus(1, ChronoUnit.DAYS))
        .thenAccept { rows ->
          log.info("User {} rebound {}", user.uid, rows)
        }
  }

  fun send(uid: Uid, payload: Object): CompletableFuture<Void> {
    log.info("Send web push message {} to uid {}", payload, uid);

    return webPushDao.getSubscriptionByUid(uid).thenCompose { subscriptionO ->
      val subscription = subscriptionO.orElseThrow {
        notFound("no push subscription", "uid", uid)
      }
      log.info("Push topic {} for uid {}", subscription.pushUrl, uid)

      pushService.sendNio(
          Notification(
              subscription.pushUrl,
              subscription.key,
              subscription.auth,
              objectMapper.writeValueAsString(payload)))
          .thenAccept { r -> log.info("Delivered to {} as {}", uid, r.statusLine) }
    }
  }
}