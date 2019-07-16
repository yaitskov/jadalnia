package org.dan.jadalnia.app.festival;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.dan.jadalnia.app.category.CategoryLink;

import java.util.Collection;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TournamentComplete {
    private FestivalState state;
    private String name;
    private Collection<CategoryLink> categories;
    private boolean hasGroups;
    private boolean hasPlayOff;
}
