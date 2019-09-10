package org.dan.jadalnia.sys.db.converters.num;

import org.dan.jadalnia.app.user.Uid;

public class UidConverter extends TypedIdConverter<Uid> {
    public UidConverter() {
        super(Uid::new);
    }

    @Override
    public Class<Uid> toType() {
        return Uid.class;
    }
}
