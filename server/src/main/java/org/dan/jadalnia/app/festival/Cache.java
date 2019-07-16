package org.dan.jadalnia.app.festival;

public interface Cache<K, V> {
    V load(K k);
    void invalidate(K k);
}
