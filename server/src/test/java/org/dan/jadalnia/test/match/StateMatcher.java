package org.dan.jadalnia.test.match;

import java.util.concurrent.CompletableFuture;

public interface StateMatcher<T> {
    boolean was(T o);
    RuntimeException report(String condition);
    CompletableFuture<T> satisfied(String condition);
}
