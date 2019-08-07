package org.dan.jadalnia.app.festival.menu;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;


@ToString
@EqualsAndHashCode
@Getter(onMethod = @__(@JsonValue))
@RequiredArgsConstructor(onConstructor = @__(@JsonCreator))
public class DishName {
    private final String name;
}
