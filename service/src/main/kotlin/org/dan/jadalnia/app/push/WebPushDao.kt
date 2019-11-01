package org.dan.jadalnia.app.push

import org.dan.jadalnia.app.label.AsyncDao
import org.dan.jadalnia.app.user.Uid
import org.dan.jadalnia.jooq.Tables.WEB_PUSH
import java.time.Instant
import java.util.*
import java.util.concurrent.CompletableFuture

class WebPushDao : AsyncDao() {
  fun register(uid: Uid, subscription: PushSubscription, expiresAt: Instant): CompletableFuture<Int> {
    return execQuery { jooq -> jooq.insertInto(WEB_PUSH,
        WEB_PUSH.P256DH,
        WEB_PUSH.UID, WEB_PUSH.EXPIRES_AT, WEB_PUSH.URL,
        WEB_PUSH.AUTH)
        .values(subscription.key, uid, expiresAt,
            subscription.pushUrl, subscription.auth).execute()
    }
  }

  fun getSubscriptionByUid(uid: Uid): CompletableFuture<Optional<PushSubscription>> {
    return execQuery { jooq -> Optional.ofNullable(jooq
        .select(WEB_PUSH.URL, WEB_PUSH.P256DH, WEB_PUSH.AUTH)
        .from(WEB_PUSH)
        .where(WEB_PUSH.UID.eq(uid))
        .fetchOne())
        .map { r -> PushSubscription(
            pushUrl = r.get(WEB_PUSH.URL),
            key = r.get(WEB_PUSH.P256DH),
            auth = r.get(WEB_PUSH.AUTH))
        }
    }
  }
}