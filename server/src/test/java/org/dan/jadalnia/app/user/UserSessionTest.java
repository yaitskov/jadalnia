package org.dan.jadalnia.app.user;

import org.junit.Test;

import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class UserSessionTest {
    @Test
    public void valueOfParse() {
        assertThat(
                UserSession.valueOf("5:c3480884-79d2-443f-a400-682fc2edbf29"),
                allOf(
                        hasProperty("uid", is(Uid.of(5))),
                        hasProperty("key", is("c3480884-79d2-443f-a400-682fc2edbf29"))));
    }

    @Test
    public void toStringJoins() {
        assertThat(
                UserSession.valueOf("5:c3480884-79d2-443f-a400-682fc2edbf29").toString(),
                is("5:c3480884-79d2-443f-a400-682fc2edbf29"));
    }
}
