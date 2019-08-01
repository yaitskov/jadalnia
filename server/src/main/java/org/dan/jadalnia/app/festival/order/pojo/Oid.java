package org.dan.jadalnia.app.festival.order.pojo;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.dan.jadalnia.sys.type.number.ImmutableNumber;

public class Oid extends ImmutableNumber {
    @JsonCreator
    public Oid(int id) {
        super(id);
    }

    // jax-rsp
    @JsonCreator
    public static Oid valueOf(String s) {
        return new Oid(Integer.valueOf(s));
    }

    public static Oid of(int id) {
        return new Oid(id);
    }
}
