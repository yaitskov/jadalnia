package org.dan.jadalnia.util.counter;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.dan.jadalnia.app.festival.order.pojo.Oid;
import org.dan.jadalnia.sys.type.number.MutableNumber;

import java.util.Optional;

public class OidSeqGen extends MutableNumber {
    @JsonCreator
    public OidSeqGen(int v) {
        super(v);
    }

    public OidSeqGen(Oid v) {
        super(v.intValue());
    }

    public Oid next() {
        return Oid.of(iGet());
    }

    public static OidSeqGen of(Optional<Oid> max) {
        return max.map(m -> new OidSeqGen(m.intValue()))
                .orElseGet(() -> new OidSeqGen(0));
    }
}
