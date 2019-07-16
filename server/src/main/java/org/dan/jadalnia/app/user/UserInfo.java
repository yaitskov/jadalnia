package org.dan.jadalnia.app.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.dan.jadalnia.app.bid.Uid;
import org.dan.jadalnia.app.festival.Fid;

@Getter
@Builder
@AllArgsConstructor
public class UserInfo implements UserLinkIf {
    private String name;
    private Uid uid;
    private Fid fid;
    private UserType userType;
    private UserState userState;

}
