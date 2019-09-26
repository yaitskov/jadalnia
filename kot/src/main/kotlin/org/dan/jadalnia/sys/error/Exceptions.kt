package org.dan.jadalnia.sys.error

object Exceptions {
    @JvmStatic
    fun rootCause(e: Throwable): Throwable {
        if (e.cause == null) {
            return e
        }
        return rootCause(e.cause as Throwable)
    }
}
