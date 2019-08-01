package org.dan.jadalnia.app.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.dan.jadalnia.app.user.Uid;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistration {
    private Uid uid;
    private String session;
    private UserType type;
}
