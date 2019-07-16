package org.dan.jadalnia.mock;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.dan.jadalnia.app.bid.Uid;

@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "session")
public class TestUserSession implements SessionAware {
    private String session;
    private Uid uid;
    private String name;
}
