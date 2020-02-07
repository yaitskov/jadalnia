package org.dan.jadalnia.sys.db.converters.json;

import org.dan.jadalnia.app.festival.menu.DishName;
import org.jooq.Converter;

public class DishNameConverter implements Converter<String, DishName> {
    @Override
    public DishName from(String name) {
        return new DishName(name);
    }

    @Override
    public String to(DishName dish) {
        return dish.getName();
    }

    @Override
    public Class<String> fromType() {
        return String.class;
    }

    @Override
    public Class<DishName> toType() {
        return DishName.class;
    }
}
