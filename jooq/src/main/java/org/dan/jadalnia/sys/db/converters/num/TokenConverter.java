package org.dan.jadalnia.sys.db.converters.num;

import org.dan.jadalnia.app.token.TokenId;


public class TokenConverter extends TypedIdConverter<TokenId> {
    public TokenConverter() {
        super(TokenId::new);
    }

    @Override
    public Class<TokenId> toType() {
        return TokenId.class;
    }
}
