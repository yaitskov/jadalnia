package org.dan.jadalnia.test.match;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

@ToString
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
public class PredicateStateMatcher<T> implements StateMatcher<T> {
    Predicate<T> pass;
    CompletableFuture<T> passedArgument;

    public static <T> PredicateStateMatcher<T> passIf(Predicate<T> pass) {
        return new PredicateStateMatcher<>(pass, new CompletableFuture<>());
    }

    public CompletableFuture<T> satisfied(String condition) {
        return passedArgument;
    }

    public boolean was(T o) {
        if (passedArgument.isDone()) {
            return true;
        }
        if (pass.test(o)) {
            passedArgument.complete(o);
            return true;
        }
        return false;
    }

    public RuntimeException report(String condition) {
        return new MatcherNotFiredException(this);
    }
}
