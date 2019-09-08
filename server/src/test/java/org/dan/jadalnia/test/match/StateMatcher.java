package org.dan.jadalnia.test.match;

import java.util.concurrent.CompletableFuture;

public interface StateMatcher<T> {
    boolean was(T o);
    RuntimeException report();
    CompletableFuture<T> satisfied();
}
