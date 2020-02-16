package org.dan.jadalnia.sys.ctx

import nl.martijndwars.webpush.PushService
import org.apache.http.impl.nio.client.HttpAsyncClients
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.dan.jadalnia.app.push.AsyncPushServiceDecorator
import org.dan.jadalnia.app.push.WebPushDao
import org.dan.jadalnia.app.push.WebPushResource
import org.dan.jadalnia.app.push.WebPushService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import java.security.Security

@Import(WebPushDao::class, WebPushService::class,
    WebPushResource::class, AsyncPushServiceDecorator::class)
class WebPushCtx {
  @Bean
  fun pushService(@Value("\${web.push.key.public}") publicKey: String,
                  @Value("\${web.push.key.private}") privateKey: String,
                  @Value("\${web.push.admin.contact.email}") adminEmail: String): PushService {
    Security.addProvider(BouncyCastleProvider());
    return PushService(publicKey, privateKey, adminEmail)
  }

  @Bean
  fun asyncHttpClient() = HttpAsyncClients.createSystem()
}