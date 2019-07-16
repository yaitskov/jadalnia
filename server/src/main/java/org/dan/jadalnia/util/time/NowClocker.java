package org.dan.jadalnia.util.time;

import static java.time.Instant.now;

import java.time.Instant;

public class NowClocker implements Clocker {
    @Override
    public Instant get() {
        return now();
    }
}
