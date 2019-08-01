package org.dan.jadalnia.app.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.dan.jadalnia.app.user.Uid;

import java.util.Optional;

@Getter
@Builder
@ToString
public class OneTimeSignInToken {
    private Uid uid;
    private Optional<String> token;
}
