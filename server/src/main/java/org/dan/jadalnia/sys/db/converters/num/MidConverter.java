package org.dan.jadalnia.sys.db.converters.num;

import org.dan.jadalnia.app.order.Oid;
import org.jooq.types.UShort;

public class MidConverter extends UShortConverter<Oid> {
    @Override
    protected Oid fromNonNull(UShort u) {
        return new Oid(u.intValue());
    }

    @Override
    public String overflowMessage() {
        return "Match id overflow";
    }

    @Override
    public Class<UShort> fromType() {
        return UShort.class;
    }

    @Override
    public Class<Oid> toType() {
        return Oid.class;
    }
}
