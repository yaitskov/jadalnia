package org.dan.jadalnia.test.match;

import com.google.common.collect.ImmutableMap;
import kotlin.Pair;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class ManyMatchers<T> implements StateMatcher<T> {
    Map<String, StateMatcher<T>> matchers;

    public ManyMatchers(String matcherA, StateMatcher<T> a,
                        String matcherB, StateMatcher<T> b) {
        this(ImmutableMap.of(matcherA, a, matcherB, b));
    }

    @Override
    public boolean was(T event) {
        matchers.values().forEach(matcher -> matcher.was(event));
        return false;
    }

    @Override
    public RuntimeException report(String condition) {
        return new MatcherNotFiredException(matchers.get(condition));
    }

    @Override
    public CompletableFuture<T> satisfied(String condition) {
        return matchers.get(condition).satisfied(condition);
    }
}
