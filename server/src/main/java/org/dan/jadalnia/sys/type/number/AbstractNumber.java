package org.dan.jadalnia.sys.type.number;

import static java.lang.Integer.compare;
import static java.lang.String.valueOf;

public abstract class AbstractNumber
        extends Number
        implements Comparable<AbstractNumber> {

    @Override
    public long longValue() {
        return intValue();
    }

    @Override
    public float floatValue() {
        return intValue();
    }

    @Override
    public double doubleValue() {
        return intValue();
    }

    @Override
    public int compareTo(AbstractNumber o) {
        return compare(intValue(), o.intValue());
    }

    public String toString() {
        return valueOf(intValue());
    }
}
