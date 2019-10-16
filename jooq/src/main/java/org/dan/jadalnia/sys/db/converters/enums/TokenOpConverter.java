package org.dan.jadalnia.sys.db.converters.enums;

import org.dan.jadalnia.app.token.TokenOp;
import org.jooq.impl.EnumConverter;

public class TokenOpConverter extends EnumConverter<String, TokenOp> {
    public TokenOpConverter() {
        super(String.class, TokenOp.class);
    }
}
