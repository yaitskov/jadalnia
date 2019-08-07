package org.dan.jadalnia.sys.db.converters.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class JsonListConverter<T> extends JsonConverter<List<T>> {
    public JsonListConverter(TypeReference<List<T>> clazz, ObjectMapper objectMapper) {
        super(clazz, objectMapper);
    }

    @Override
    public Class<List<T>> toType() {
        return (Class<List<T>>) (Object) List.class;
    }
}
