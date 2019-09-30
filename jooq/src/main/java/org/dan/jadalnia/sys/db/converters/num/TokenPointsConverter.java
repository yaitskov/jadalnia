package org.dan.jadalnia.sys.db.converters.num;

import org.dan.jadalnia.app.token.TokenPoints;

public class TokenPointsConverter extends TypedIdConverter<TokenPoints> {
    public TokenPointsConverter() {
        super(TokenPoints::new);
    }

    @Override
    public Class<TokenPoints> toType() {
        return TokenPoints.class;
    }
}
