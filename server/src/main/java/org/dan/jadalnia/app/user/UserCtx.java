package org.dan.jadalnia.app.user;

import org.springframework.context.annotation.Import;

@Import({UserResource.class, UserDao.class})
public class UserCtx {
}
