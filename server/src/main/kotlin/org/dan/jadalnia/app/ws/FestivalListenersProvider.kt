package org.dan.jadalnia.app.ws

import org.dan.jadalnia.app.festival.pojo.Fid
import org.springframework.context.annotation.Bean

import java.util.concurrent.ConcurrentHashMap

class FestivalListenersProvider {
  @Bean
  fun getMap(): MutableMap<Fid, FestivalListeners> {
    return ConcurrentHashMap()
  }
}
