package org.dan.jadalnia.util.collection;

public class CounterInt {
    private int v;

    public void inc() {
        ++v;
    }

    public int postInc() {
        return v++;
    }

    public int toInt() {
        return v;
    }
}
