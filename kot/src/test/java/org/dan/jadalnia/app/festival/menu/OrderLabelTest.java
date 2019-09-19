package org.dan.jadalnia.app.festival.menu;

import org.dan.jadalnia.app.order.pojo.OrderLabel;
import org.hamcrest.core.Is;
import org.junit.Test;

import static org.junit.Assert.assertThat;

public class OrderLabelTest {
    @Test
    public void marshalingStable() {
        for (int i = 0; i < 222; ++i) {
            OrderLabel originLabel = OrderLabel.of(i);
            String textView = originLabel.toString();
            assertThat(i, Is.is(new OrderLabel(textView).getId()));
            assertThat(textView.matches("^[A-Z][0-9]+"), Is.is(true));
        }
    }

    @Test
    public void serialize() {
        assertThat(new OrderLabel("A1").toString(), Is.is("A1"));
    }
}
