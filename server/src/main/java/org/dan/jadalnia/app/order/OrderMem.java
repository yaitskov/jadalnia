package org.dan.jadalnia.app.order;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.dan.jadalnia.app.user.Cid;

@Getter
@Builder
@FieldDefaults(makeFinal = true)
@RequiredArgsConstructor
public class OrderMem {
    Cid customerId;
}
