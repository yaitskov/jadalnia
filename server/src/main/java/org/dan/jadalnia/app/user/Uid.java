package org.dan.jadalnia.app.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.dan.jadalnia.sys.type.number.ImmutableNumber;

public class Uid extends ImmutableNumber {
    @JsonCreator
    public Uid(int id) {
        super(id);
    }

    // jax-rsp
    @JsonCreator
    public static Uid valueOf(String s) {
        return new Uid(Integer.valueOf(s));
    }

    public static Uid of(int id) {
        return new Uid(id);
    }
}
