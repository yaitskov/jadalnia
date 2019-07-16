package org.dan.jadalnia.sys.db.converters.num;

import static java.util.Optional.ofNullable;

import org.dan.jadalnia.app.order.Oid;
import org.jooq.Converter;
import org.jooq.types.UShort;

import java.util.Optional;

public class OptionalMidConverter implements Converter<UShort, Optional<Oid>> {
    public Optional<Oid> from(UShort t) {
        return ofNullable(t).map(UShort::intValue).map(Oid::new);
    }

    public UShort to(Optional<Oid> t) {
        return t.map(Oid::shortValue)
                .map(UShort::valueOf)
                .orElse(null);
    }

    @Override
    public Class<UShort> fromType() {
        return UShort.class;
    }

    @Override
    public Class<Optional<Oid>> toType() {
        return (Class<Optional<Oid>>) Optional.empty().getClass();
    }
}