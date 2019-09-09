package org.dan.jadalnia.sys;

import org.hamcrest.core.Is;
import org.junit.Test;

import static org.junit.Assert.assertThat;

public class KotloTest {
    @Test
    public void returnHello() {
        assertThat(new Kotlo().hello(), Is.is("HELLO"));
    }
}
