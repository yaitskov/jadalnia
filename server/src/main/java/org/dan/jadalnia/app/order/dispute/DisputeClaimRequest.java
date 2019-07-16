package org.dan.jadalnia.app.order.dispute;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.dan.jadalnia.app.bid.Bid;
import org.dan.jadalnia.app.order.Oid;
import org.dan.jadalnia.app.festival.Fid;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DisputeClaimRequest {
    private Fid fid;
    private Oid oid;
    private Map<Bid, List<Integer>> sets;
    private Optional<String> comment;
}
