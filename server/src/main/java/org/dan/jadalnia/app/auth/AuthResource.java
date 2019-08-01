package org.dan.jadalnia.app.auth;

import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Slf4j
@Path("/")
@Produces(APPLICATION_JSON)
public class AuthResource {
    public static final String AUTH_BY_ONE_TIME_TOKEN = "/anonymous/auth/by-one-time-token/";
    public static final String AUTH_GENERATE_SIGN_IN_LINK = "/anonymous/auth/generate/sign-in-link";
    public static final String DEV_CLEAN_SIGN_IN_TOKEN_TABLE = "/dev/clean-sign-in-token-table";

    @Inject
    private AuthService authService;

 }
