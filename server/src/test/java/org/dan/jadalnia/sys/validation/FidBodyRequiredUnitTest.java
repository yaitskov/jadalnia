package org.dan.jadalnia.sys.validation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.dan.jadalnia.app.festival.Fid;
import org.dan.jadalnia.sys.validation.FidBodyRequired.Validator;
import org.junit.Test;

public class FidBodyRequiredUnitTest {
    @Test
    public void shouldFailOnNull() {
        assertThat(new Validator().isValid(null, null), is(false));
    }

    @Test
    public void shouldPass() {
        assertThat(new Validator().isValid(Fid.of(1), null), is(true));
    }
}
