package org.dan.jadalnia.app.user;

import org.dan.jadalnia.sys.error.JadEx.Companion.badRequest
import org.dan.jadalnia.sys.error.TemplateError
import java.util.Collections.singletonMap

data class UserSession(val uid: Uid, val key: String) {
    companion object {
        fun valueOf(s: String): UserSession {
            val parts = s.split(Regex(":"), 2);
            try {
                return UserSession(
                        uid = Uid.valueOf(parts[0]),
                        key = parts[1])
            } catch (e: NumberFormatException) {
                throw badRequest(
                        TemplateError("user session has wrong format: [\$session]",
                                singletonMap("session", s)), e);
            }
        }
    }

    override fun toString(): String = uid + ":" + key
}
