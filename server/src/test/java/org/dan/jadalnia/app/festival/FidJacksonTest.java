package org.dan.jadalnia.app.festival;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.dan.jadalnia.app.festival.pojo.Fid;
import org.dan.jadalnia.sys.ctx.jackson.ObjectMapperProvider;
import org.junit.Test;

import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class FidJacksonTest {
    @Test
    @SneakyThrows
    public void scalar() {
        ObjectMapper om = ObjectMapperProvider.Companion.get();
        final String actual = om.writeValueAsString(new Fid(123));
        assertThat(actual, is("123"));
        assertThat(om.readValue(actual, Fid.class),
                hasProperty("validateValue", is(123)));
    }
}
