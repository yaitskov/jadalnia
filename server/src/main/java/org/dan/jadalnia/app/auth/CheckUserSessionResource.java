package org.dan.jadalnia.app.auth;

import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.ws.rs.Path;

@Slf4j
@Path(CheckUserSessionResource.AUTH_USER_CHECK_SESSION)
public class CheckUserSessionResource {
    public static final String AUTH_USER_CHECK_SESSION = "/auth/user/check/session";

    @Inject
    private AuthService authService;

 }
