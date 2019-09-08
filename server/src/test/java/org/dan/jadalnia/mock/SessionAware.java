package org.dan.jadalnia.mock;

import org.dan.jadalnia.app.user.UserSession;

public interface SessionAware {
    UserSession getSession();
}
