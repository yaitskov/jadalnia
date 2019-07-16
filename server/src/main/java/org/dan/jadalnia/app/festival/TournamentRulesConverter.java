package org.dan.jadalnia.app.festival;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.dan.jadalnia.sys.ctx.jackson.ObjectMapperProvider;
import org.jooq.Converter;

public class TournamentRulesConverter implements Converter<String, FestivalMenu> {
    private static final ObjectMapper mapper = ObjectMapperProvider.get();

    @Override
    @SneakyThrows
    public FestivalMenu from(String databaseObject) {
        return mapper.readValue(databaseObject, FestivalMenu.class);
    }

    @Override
    @SneakyThrows
    public String to(FestivalMenu userObject) {
        return mapper.writeValueAsString(userObject);
    }

    @Override
    public Class<String> fromType() {
        return String.class;
    }

    @Override
    public Class<FestivalMenu> toType() {
        return FestivalMenu.class;
    }
}
