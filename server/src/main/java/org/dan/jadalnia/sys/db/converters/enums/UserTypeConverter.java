package org.dan.jadalnia.sys.db.converters.enums;

import org.dan.jadalnia.app.user.UserType;
import org.jooq.impl.EnumConverter;

public class UserTypeConverter extends EnumConverter<String, UserType> {
    public UserTypeConverter() {
        super(String.class, UserType.class);
    }
}
