package org.dan.jadalnia.app.user;

import org.jooq.impl.EnumConverter;

public class UserStateConverter extends EnumConverter<String, UserState> {
    public UserStateConverter() {
        super(String.class, UserState.class);
    }
}
