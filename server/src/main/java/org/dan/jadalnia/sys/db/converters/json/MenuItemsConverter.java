package org.dan.jadalnia.sys.db.converters.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dan.jadalnia.app.festival.menu.MenuItem;

import java.util.List;

public class MenuItemsConverter extends JsonConverter<List<MenuItem>> {
    public MenuItemsConverter() {
        super(new TypeReference<List<MenuItem>>() {}, new ObjectMapper());
    }
}
