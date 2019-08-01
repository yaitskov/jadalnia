package org.dan.jadalnia.sys.db.converters.enums;

import org.dan.jadalnia.app.festival.pojo.FestivalState;
import org.jooq.impl.EnumConverter;

public class FestivalStateConverter extends EnumConverter<String, FestivalState> {
    public FestivalStateConverter() {
        super(String.class, FestivalState.class);
    }
}
