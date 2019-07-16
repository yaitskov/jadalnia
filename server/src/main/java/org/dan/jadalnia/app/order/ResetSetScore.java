package org.dan.jadalnia.app.order;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dan.jadalnia.app.festival.Fid;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetSetScore {
    private Fid fid;
    private Oid oid;
    private int setNumber;
}
