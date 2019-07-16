package org.dan.jadalnia.app.user;

import org.jooq.impl.EnumConverter;

public class UserTypeConverter extends EnumConverter<String, UserType> {
    public UserTypeConverter() {
        super(String.class, UserType.class);
    }
}
