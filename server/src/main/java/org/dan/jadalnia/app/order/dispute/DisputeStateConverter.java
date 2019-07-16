package org.dan.jadalnia.app.order.dispute;

import org.jooq.impl.EnumConverter;

public class DisputeStateConverter extends EnumConverter<String, DisputeStatus> {
    public DisputeStateConverter() {
        super(String.class, DisputeStatus.class);
    }
}
