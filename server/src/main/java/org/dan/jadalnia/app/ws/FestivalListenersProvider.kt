package org.dan.jadalnia.app.ws

//import java.util.Map
import org.dan.jadalnia.app.festival.pojo.Fid
import org.springframework.context.annotation.Bean

//import javax.inject.Provider
import java.util.concurrent.ConcurrentHashMap

class FestivalListenersProvider {
    @Bean
    public fun getMap(): MutableMap<Fid, FestivalListeners> {
        return ConcurrentHashMap() //<Fid, FestivalListeners>() as Map<Fid, FestivalListeners>)
    }
}
