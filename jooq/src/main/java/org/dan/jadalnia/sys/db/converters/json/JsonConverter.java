package org.dan.jadalnia.sys.db.converters.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jooq.Converter;


import static org.dan.jadalnia.util.Reflector.genericSuperClass;

@RequiredArgsConstructor
public class JsonConverter<T> implements Converter<String, T> {
    private final TypeReference<T> clazz;
    private final ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    public T from(String s) {
        if (s == null) {
            return null;
        }
        return objectMapper.readValue(s, clazz);
    }

    @Override
    @SneakyThrows
    public String to(T l) {
        if (l == null) {
            return null;
        }
        return objectMapper.writeValueAsString(l);
    }

    @Override
    public Class<String> fromType() {
        return String.class;
    }

    @Override
    public Class<T> toType() {
        return genericSuperClass(clazz.getClass());
    }
}
