package org.dan.jadalnia.app.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.dan.jadalnia.app.user.Uid;
import org.dan.jadalnia.app.user.UserType;

@Getter
@Builder
@ToString
public class NameAndUid {
    private Uid uid;
    private String name;
    private UserType type;
}
