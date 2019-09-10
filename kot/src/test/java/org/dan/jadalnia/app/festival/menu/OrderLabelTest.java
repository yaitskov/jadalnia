package org.dan.jadalnia.app.festival.menu;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dan.jadalnia.app.order.pojo.OrderLabel;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;

import static org.junit.Assert.assertThat;

public class OrderLabelTest {
    @Test
    public void marshalingStable() {
        for (int i = 0; i < 222; ++i) {
            OrderLabel originLabel = OrderLabel.of(i);
            String textView = originLabel.toString();
            assertThat(i, Is.is(OrderLabel.ofJson(textView).getId()));
            assertThat(textView.matches("^[A-Z][0-9]+"), Is.is(true));
        }
    }
}
