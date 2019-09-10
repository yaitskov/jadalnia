package org.dan.jadalnia.sys.db.converters.num;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.dan.jadalnia.sys.type.number.ImmutableNumber;
import org.jooq.Converter;

import java.util.function.Function;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public abstract class TypedIdConverter
        <JT extends ImmutableNumber>
        implements Converter<Integer, JT> {
    Function<Integer, JT> factory;

    @Override
    public JT from(Integer c) {
        if (c == null || c == 0) {
            return null;
        }
        return factory.apply(c);
    }

    @Override
    public Integer to(JT u) {
        if (u == null) {
            return null;
        }
        return u.intValue();
    }

    @Override
    public Class<Integer> fromType() {
        return Integer.class;
    }
}
