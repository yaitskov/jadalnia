package org.dan.jadalnia.sys.db.converters.num;

import org.dan.jadalnia.app.bid.Bid;
import org.jooq.types.UShort;

public class BidConverter extends UShortConverter<Bid> {
    @Override
    protected Bid fromNonNull(UShort u) {
        return new Bid(u.intValue());
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
    public Class<Bid> toType() {
        return Bid.class;
    }
}
