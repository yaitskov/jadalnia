package org.dan.jadalnia.app.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.dan.jadalnia.sys.type.number.ImmutableNumber;

public class Cid extends ImmutableNumber {
    @JsonCreator
    public Cid(int id) {
        super(id);
    }

    // jax-rsp
    @JsonCreator
    public static Cid valueOf(String s) {
        return new Cid(Integer.valueOf(s));
    }

    public static Cid of(int id) {
        return new Cid(id);
    }
}
