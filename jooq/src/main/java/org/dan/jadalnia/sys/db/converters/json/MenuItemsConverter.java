package org.dan.jadalnia.sys.db.converters.json;

import com.fasterxml.jackson.core.type.TypeReference;
import org.dan.jadalnia.app.festival.menu.MenuItem;
import org.dan.jadalnia.sys.jackson.ObjectMapperFactory;

import java.util.List;

public class MenuItemsConverter extends JsonListConverter<MenuItem> {
    public MenuItemsConverter() {
        super(
                new TypeReference<List<MenuItem>>() {},
                ObjectMapperFactory.INSTANCE.create());
    }
}
