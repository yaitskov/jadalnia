package org.dan.jadalnia.app.festival;

import static java.util.Collections.emptySet;
import static javax.validation.Validation.buildDefaultValidatorFactory;
import static org.dan.jadalnia.app.festival.Fid.FESTIVAL_ID_SHOULD_BE_A_POSITIVE_NUMBER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import javax.validation.Validator;

public class FidValidationTest {
    Validator validator = buildDefaultValidatorFactory().getValidator();

    @Test
    public void shouldPass() {
        assertThat(validator.validate(Fid.of(1)), is(emptySet()));
    }

    @Test
    public void shouldFailOnZeroId() {
        assertThat(validator.validate(Fid.of(0)),
                hasItem(hasProperty("message", is(FESTIVAL_ID_SHOULD_BE_A_POSITIVE_NUMBER))));
    }

    @Test
    public void shouldFailOnNegativeId() {
        assertThat(validator.validate(Fid.of(-1)),
                hasItem(hasProperty("message", is(FESTIVAL_ID_SHOULD_BE_A_POSITIVE_NUMBER))));
    }
}
