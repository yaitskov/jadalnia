package org.dan.jadalnia.app.festival;

import org.jooq.impl.EnumConverter;

public class FestivalStateConverter extends EnumConverter<String, FestivalState> {
    public FestivalStateConverter() {
        super(String.class, FestivalState.class);
    }
}
