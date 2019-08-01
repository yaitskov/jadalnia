package org.dan.jadalnia.sys.db.converters.num;

import org.dan.jadalnia.app.user.Cid;

public class CidConverter extends TypedIdConverter<Cid> {
    public CidConverter() {
        super(Cid::new);
    }

    @Override
    public Class<Cid> toType() {
        return Cid.class;
    }
}
