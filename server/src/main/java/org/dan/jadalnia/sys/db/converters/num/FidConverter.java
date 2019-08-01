package org.dan.jadalnia.sys.db.converters.num;

import org.dan.jadalnia.app.festival.pojo.Fid;

public class FidConverter extends TypedIdConverter<Fid> {
    public FidConverter() {
        super(Fid::new);
    }

    @Override
    public Class<Fid> toType() {
        return Fid.class;
    }
}
