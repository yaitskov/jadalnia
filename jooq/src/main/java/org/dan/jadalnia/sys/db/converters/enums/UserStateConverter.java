package org.dan.jadalnia.sys.db.converters.enums;

import org.dan.jadalnia.app.user.UserState;
import org.jooq.impl.EnumConverter;

public class UserStateConverter extends EnumConverter<String, UserState> {
    public UserStateConverter() {
        super(String.class, UserState.class);
    }
}
