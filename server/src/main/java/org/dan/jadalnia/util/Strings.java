package org.dan.jadalnia.util;

public class Strings {
    public static String cutLongerThan(String s, int limit) {
        if (s.length() <= limit) {
            return s;
        }
        return s.substring(0, limit);
    }
}
