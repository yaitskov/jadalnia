package org.dan.jadalnia.app.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.dan.jadalnia.sys.ctx.jackson.ObjectMapperProvider;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@Slf4j
public class UidJacksonTest {
    static final ObjectMapper om = ObjectMapperProvider.Companion.get();

    @Test
    @SneakyThrows
    public void scalar() {
        final String actual = om.writeValueAsString(new Uid(123));
        assertThat(actual, is("123"));
        assertThat(om.readValue(actual, Uid.class),
                hasProperty("validateValue", is(123)));
    }

    @Test
    @SneakyThrows
    public void mapKey() {
        try {
            final ImmutableMap<Uid, String> origin = ImmutableMap.of(new Uid(123), "x");
            final String actual = om.writeValueAsString(origin);
            assertThat(actual, is("{\"123\":\"x\"}"));
            assertThat(om.readValue(actual, new TypeReference<Map<Uid, String>>() {
                    }),
                    is(origin));
        } catch (Exception e) {
            log.error("dd", e);
            throw  e;
        }
    }
}
