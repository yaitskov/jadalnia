package org.dan.jadalnia.app.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dan.jadalnia.app.bid.ParticipantLink;
import org.dan.jadalnia.app.category.CategoryLink;
import org.dan.jadalnia.app.table.TableLink;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenMatchForWatch {
    private Oid oid;
    private Optional<TableLink> table;
    private List<ParticipantLink> participants;
    private Instant started;
    private MatchType type;
    private List<Integer> score;
    private CategoryLink category;
}
