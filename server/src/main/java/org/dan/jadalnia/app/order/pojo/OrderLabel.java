package org.dan.jadalnia.app.order.pojo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;


@ToString
@Getter(onMethod = @__(@JsonValue))
@RequiredArgsConstructor(onConstructor = @__(@JsonCreator))
public class OrderLabel {
    private final String name;
}
