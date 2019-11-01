package org.dan.jadalnia.util.time

import java.time.Instant

interface Clocker {
  fun get(): Instant
}
