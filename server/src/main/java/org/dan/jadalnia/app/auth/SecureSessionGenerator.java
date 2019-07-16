package org.dan.jadalnia.app.auth;

import java.util.UUID;

public class SecureSessionGenerator implements SessionGenerator {
    @Override
    public String generate() {
        return UUID.randomUUID().toString().substring(0, 10);
    }
}
