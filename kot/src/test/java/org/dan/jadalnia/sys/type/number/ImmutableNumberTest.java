package org.dan.jadalnia.sys.type.number;

import org.hamcrest.core.Is;
import org.junit.Test;

import static org.junit.Assert.assertThat;

public class ImmutableNumberTest {
    @Test
    public void equals() {
        assertThat(new ImmutableNumber(1),
                Is.is(new ImmutableNumber(1)));
    }
}
