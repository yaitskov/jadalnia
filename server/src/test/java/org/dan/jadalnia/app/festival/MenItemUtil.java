package org.dan.jadalnia.app.festival;

import org.dan.jadalnia.app.festival.menu.DishName;
import org.dan.jadalnia.app.festival.menu.MenuItem;

import static java.util.Collections.emptyList;

public class MenItemUtil {
    public static MenuItem ofDish(String dishName) {
        return new MenuItem(
                new DishName(dishName), "**", 1.0, false, emptyList());
    }
}
