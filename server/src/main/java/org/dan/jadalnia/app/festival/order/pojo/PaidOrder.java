package org.dan.jadalnia.app.festival.order.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaidOrder {
    private Oid orderNumber;
    private OrderLabel orderLabel;
    private List<OrderItem> items;
}
