package org.dan.jadalnia.sys.db.converters.num;

import org.dan.jadalnia.app.festival.Fid;
import org.dan.jadalnia.app.festival.Fid;
import org.jooq.Converter;

public class FidConverter implements Converter<Integer, Fid> {
    @Override
    public Fid from(Integer uid) {
        if (uid == null || uid == 0) {
            return null;
        }
        return new Fid(uid);
    }

    @Override
    public Integer to(Fid u) {
        if (u == null) {
            return null;
        }
        return u.getTid();
    }

    @Override
    public Class<Integer> fromType() {
        return Integer.class;
    }

    @Override
    public Class<Fid> toType() {
        return Fid.class;
    }
}
