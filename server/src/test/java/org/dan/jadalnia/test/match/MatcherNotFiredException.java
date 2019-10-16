package org.dan.jadalnia.test.match;

import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(makeFinal = true)
public class MatcherNotFiredException extends RuntimeException {
    StateMatcher matcher;

    public MatcherNotFiredException(StateMatcher matcher) {
        super("State matcher " + matcher + " did not fired");
        this.matcher = matcher;
    }
}
