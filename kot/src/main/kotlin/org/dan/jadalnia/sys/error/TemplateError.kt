package org.dan.jadalnia.sys.error;

class TemplateError(
        message: String,
        val params: MutableMap<String, Any>)
    :
        Error(message) {

    override fun toString(): String {
        return "template error: id=[$id] msg=[$message] params=[$params]"
    }
}

