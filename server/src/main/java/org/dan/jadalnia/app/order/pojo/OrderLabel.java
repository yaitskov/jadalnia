package org.dan.jadalnia.app.order.pojo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.val;


@ToString
@Getter(onMethod = @__(@JsonValue))
@RequiredArgsConstructor(onConstructor = @__(@JsonCreator))
public class OrderLabel {
    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUWXYZ";

    private final int id;
    private final String name;

    public OrderLabel(int id) {
        this.id = id;
        val letterIdx = id % LETTERS.length();
        name = "" + LETTERS.charAt(letterIdx) + (1 + id / LETTERS.length());
    }

    public static OrderLabel of(int labelId) {
        return new OrderLabel(labelId);
    }
}
