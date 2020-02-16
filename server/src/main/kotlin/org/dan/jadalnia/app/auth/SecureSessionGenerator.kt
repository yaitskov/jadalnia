package org.dan.jadalnia.app.auth

import java.util.UUID

class SecureSessionGenerator : SessionGenerator {
    override fun generate(): String {
        return UUID.randomUUID().toString().substring(0, 10)
    }
}
