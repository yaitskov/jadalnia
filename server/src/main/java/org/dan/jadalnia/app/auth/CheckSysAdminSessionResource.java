package org.dan.jadalnia.app.auth;

import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.Path;

@Slf4j
@Path(CheckSysAdminSessionResource.ANONYMOUS_SYS_ADMIN_CHECK + "{session}")
public class CheckSysAdminSessionResource {
    static final String ANONYMOUS_SYS_ADMIN_CHECK = "/anonymous/sys/admin/check/";


}
