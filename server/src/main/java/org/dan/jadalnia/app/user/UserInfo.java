package org.dan.jadalnia.app.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.dan.jadalnia.app.festival.pojo.Fid;
import org.dan.jadalnia.sys.error.Error;
import org.dan.jadalnia.sys.error.JadEx;

@Getter
@Builder
@AllArgsConstructor
public class UserInfo implements UserLinkIf {
    private String name;
    private Uid uid;
    private Fid fid;
    private UserType userType;
    private UserState userState;

    public UserInfo ensureAdmin() {
        if (userType != UserType.Admin) {
            throw new JadEx(401, new Error("user is not admin"), null);
        }
        return this;
    }
}
