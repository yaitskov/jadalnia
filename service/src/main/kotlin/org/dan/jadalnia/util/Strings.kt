package org.dan.jadalnia.util

class Strings {
    companion object {
        fun cutLongerThan(s: String, limit: Int): String {
            if (s.length <= limit) {
                return s
            }
            return s.substring(0, limit)
        }
    }
}
