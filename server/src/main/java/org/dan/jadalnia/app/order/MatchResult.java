package org.dan.jadalnia.app.order;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.dan.jadalnia.app.bid.ParticipantLink;
import org.dan.jadalnia.app.category.CategoryLink;
import org.dan.jadalnia.app.group.GroupLink;
import org.dan.jadalnia.app.festival.Fid;
import org.dan.jadalnia.app.user.UserState;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MatchResult {
    private Fid fid;
    private int playedSets;
    private MatchScore score;
    private List<ParticipantLink> participants;
    private MyPendingMatchSport sport;
    private int disputes;
    private MatchState state;
    private MatchType type;
    private Optional<GroupLink> group = Optional.empty();
    private CategoryLink category;
    private UserState role;

    public static class MatchResultBuilder {
        Optional<GroupLink> group = Optional.empty();
    }
}
