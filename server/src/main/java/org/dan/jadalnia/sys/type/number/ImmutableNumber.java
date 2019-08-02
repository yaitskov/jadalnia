package org.dan.jadalnia.sys.type.number;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@RequiredArgsConstructor(onConstructor = @__(@JsonCreator))
public class ImmutableNumber extends AbstractNumber {
    private final int value;

    @Override
    @JsonValue
    public int intValue() {
        return value;
    }

    @JsonIgnore
    public int getValidateValue() {
        return value;
    }
}
