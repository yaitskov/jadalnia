package org.dan.jadalnia.sys.ctx

import java.beans.PropertyEditorSupport

abstract class TypeSafePropertyEditor<T> : PropertyEditorSupport() {
    override fun setAsText(text: String) {
        value = getValueFromString(text)
    }

    protected abstract fun getValueFromString(text: String): T
}
