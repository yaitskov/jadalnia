package org.dan.jadalnia.sys.error;

class TemplateError(
        message: String,
        val params: MutableMap<String, Any>)
    :
        Error(message)

