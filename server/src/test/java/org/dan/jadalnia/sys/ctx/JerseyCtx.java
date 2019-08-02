package org.dan.jadalnia.sys.ctx;

import org.dan.jadalnia.sys.ctx.jackson.ObjectMapperContextResolver;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.springframework.context.annotation.Bean;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

public class JerseyCtx {
    @Bean
    public Client client(ObjectMapperContextResolver resolver) {
        return ClientBuilder.newBuilder()
                .register(resolver)
                .register(JacksonFeature.class)
                .build();
    }
}
