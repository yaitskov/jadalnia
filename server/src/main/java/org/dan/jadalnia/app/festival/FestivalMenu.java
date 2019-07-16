package org.dan.jadalnia.app.festival;

import static org.dan.jadalnia.app.castinglots.rank.GroupSplitPolicy.BalancedMix;
import static org.dan.jadalnia.app.group.ConsoleTournament.NO;
import static org.dan.jadalnia.sys.error.JadalniaEx.internalError;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Wither;
import org.dan.jadalnia.app.castinglots.rank.CastingLotsRule;
import org.dan.jadalnia.app.group.ConsoleTournament;
import org.dan.jadalnia.app.group.GroupRules;
import org.dan.jadalnia.app.place.ArenaDistributionPolicy;
import org.dan.jadalnia.app.place.PlaceRules;
import org.dan.jadalnia.app.playoff.PlayOffRule;
import org.dan.jadalnia.app.sport.MatchRules;

import java.util.Optional;

@Getter
@Setter
@Builder
@Wither
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FestivalMenu {
    public static final int FIRST_VERSION = 2;
    @JsonProperty("v")
    private int version = FIRST_VERSION;
    private MatchRules match;
    private CastingLotsRule casting;
    private Optional<PlayOffRule> playOff = Optional.empty();
    private Optional<GroupRules> group = Optional.empty();
    private Optional<PlaceRules> place = Optional.empty();
    private Optional<RewardRules> rewards = Optional.empty();
    private Optional<EnlistPolicy> enlist = Optional.empty();

    public static class TournamentRulesBuilder {
        int version = FIRST_VERSION;
        Optional<EnlistPolicy> enlist = Optional.empty();
        Optional<PlayOffRule> playOff = Optional.empty();
        Optional<GroupRules> group = Optional.empty();
        Optional<RewardRules> rewards = Optional.empty();
        Optional<PlaceRules> place = Optional.empty();
        CastingLotsRule casting = CastingLotsRule.builder()
                .splitPolicy(BalancedMix)
                .build();
    }

    public boolean consoleInGroupP() {
        return group.map(GroupRules::getConsole).orElse(NO) != NO;
    }

    public GroupRules group(Fid fid) {
        return group.orElseThrow(() -> internalError("tournament " + fid + " without groups"));
    }

    public PlayOffRule playOff(Fid fid) {
        return playOff.orElseThrow(() -> internalError("tournament " + fid + " without playoff"));
    }

    public ArenaDistributionPolicy arenaDistribution() {
        return place.map(PlaceRules::getArenaDistribution)
                .orElse(ArenaDistributionPolicy.NO);
    }

    public ConsoleTournament consoleGroup() {
        return group.map(GroupRules::getConsole)
                .orElse(NO);
    }

    public ConsoleTournament consolePlayOff() {
        return playOff.map(PlayOffRule::getConsole)
                .orElse(NO);
    }
}
