package org.dan.jadalnia.app.festival;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.dan.jadalnia.sys.type.number.ImmutableNumber;

import javax.validation.constraints.Min;

public class Fid extends ImmutableNumber {
    public static final String FESTIVAL_ID_SHOULD_BE_A_POSITIVE_NUMBER = "festival id should be a positive number";

    @JsonCreator
    public Fid(int value) {
        super(value);
    }

    @Min(value = 1, message = FESTIVAL_ID_SHOULD_BE_A_POSITIVE_NUMBER)
    public int getValidateValue() {
        return super.getValidateValue();
    }

    // jax-rsp
    @JsonCreator
    public static Fid valueOf(String s) {
        return new Fid(Integer.valueOf(s));
    }

    public static Fid of(int id) {
        return new Fid(id);
    }

    @JsonIgnore
    public int getTid() {
        return intValue();
    }
}
