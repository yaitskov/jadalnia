package org.dan.jadalnia.app.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SysAdmin {
    private int id;
    private String login;
    private String salt;
    private String passwordHash;
}
