package org.dan.jadalnia;

import lombok.RequiredArgsConstructor;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import static org.hamcrest.core.Is.is;

public class LombokTest {
    @RequiredArgsConstructor
    public static class X {
        @Value("ddd")
        private final int f;
    }
    @Test
    public void copyValueAnnotationToConstructor() {
        Assert.assertThat(
                ((Value) X.class.getConstructors()[0].getParameterAnnotations()[0][0]).value(),
                is("ddd"));
    }
}
