package org.dan.jadalnia.sys.db.converters.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JsonConverterTest {
    class X {
    }

    class ConverterX extends JsonConverter<X> {
        public ConverterX() {
            super(
                    new TypeReference<X>() {},
                    new ObjectMapper());
        }
    }

    @Test
    public void extractParameterClass() {
        assertThat(new ConverterX().toType(), is(X.class));
    }
}
