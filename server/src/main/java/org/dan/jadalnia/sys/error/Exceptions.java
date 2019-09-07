package org.dan.jadalnia.sys.error;

public class Exceptions {
    public static Throwable rootCause(Throwable e) {
        if (e.getCause() == null) {
            return e;
        }
        return rootCause(e.getCause());
    }
}
