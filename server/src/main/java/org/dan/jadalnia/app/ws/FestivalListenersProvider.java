package org.dan.jadalnia.app.ws;

import org.dan.jadalnia.app.festival.pojo.Fid;
import org.springframework.context.annotation.Bean;

import javax.inject.Provider;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FestivalListenersProvider implements Provider<Map<Fid, FestivalListeners>> {
    @Bean
    public Map<Fid, FestivalListeners> get() {
        return new ConcurrentHashMap<>();
    }
}
