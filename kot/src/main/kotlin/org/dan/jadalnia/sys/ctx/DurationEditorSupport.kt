package org.dan.jadalnia.sys.ctx

import java.time.Duration

class DurationEditorSupport : TypeSafePropertyEditor<Duration>() {
    override fun getValueFromString(text: String): Duration {
        return Duration.parse(text.toUpperCase())
    }
}
