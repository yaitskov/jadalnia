package org.dan.jadalnia.sys.type.number;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor(onConstructor = @__(@JsonCreator))
public class MutableNumber extends AbstractNum {
    private int value;

    @Override
    @JsonValue
    public int intValue() {
        return value;
    }

    public int iGet() {
        return ++value;
    }
}
