package org.dan.jadalnia.sys.db.converters.num;

import org.dan.jadalnia.app.order.pojo.Oid;

public class OidConverter extends TypedIdConverter<Oid> {
    public OidConverter() {
        super(Oid::new);
    }

    @Override
    public Class<Oid> toType() {
        return Oid.class;
    }
}
