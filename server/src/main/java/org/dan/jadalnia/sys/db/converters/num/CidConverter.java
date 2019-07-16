package org.dan.jadalnia.sys.db.converters.num;

import org.dan.jadalnia.app.category.Cid;
import org.jooq.types.UByte;

public class CidConverter extends UByteConverter<Cid> {
    @Override
    protected Cid fromNonNull(UByte u) {
        return new Cid(u.intValue());
    }

    @Override
    public String overflowMessage() {
        return "Category id overflow";
    }

    @Override
    public Class<Cid> toType() {
        return Cid.class;
    }
}
