package org.dan.jadalnia.sys.error;

import org.eclipse.jetty.http.HttpStatus.BAD_REQUEST_400
import org.eclipse.jetty.http.HttpStatus.FORBIDDEN_403
import org.eclipse.jetty.http.HttpStatus.INTERNAL_SERVER_ERROR_500
import org.eclipse.jetty.http.HttpStatus.NOT_FOUND_404
import org.eclipse.jetty.http.HttpStatus.UNAUTHORIZED_401

import com.google.common.collect.ImmutableMap


class JadEx : RuntimeException {
    val status: Int
    val error: Error

    constructor(status: Int, error: Error, cause: Throwable?)
            : super(error.toString(), cause) {
        this.status = status;
        this.error = error;
    }

    constructor(status: Int, message: Error): this(status, message, null)

    companion object {
        @JvmStatic
        fun notFound(message: String) = JadEx(NOT_FOUND_404, Error(message))

        @JvmStatic
        fun notFound(template: String, param: String, value: Any)
                = JadEx(NOT_FOUND_404, TemplateError(template, ImmutableMap.of(param, value)))

        @JvmStatic
        fun notAuthorized(msg: String) = JadEx(UNAUTHORIZED_401, Error(msg))

        @JvmStatic
        fun forbidden(template: String, param: String, value: Any)
                = JadEx(FORBIDDEN_403, TemplateError(template, ImmutableMap.of(param, value)))

        @JvmStatic
        fun forbidden(message: String) = JadEx(FORBIDDEN_403, Error(message))

        @JvmStatic
        fun badRequest(message: String) = badRequest(Error(message))

        @JvmStatic
        fun badRequest(message: String, e: Exception) = badRequest(Error(message), e)

        @JvmStatic
        fun badRequest(error: Error) = badRequest(error, null)

        @JvmStatic
        fun badRequest(template: String, params: MutableMap<String, Any>)
                = badRequest(TemplateError(template, params))

        @JvmStatic
        fun badRequest(template: String, param: String, value: Any)
                = badRequest(template, ImmutableMap.of(param, value))

        @JvmStatic
        fun badRequest(clientMessage: Error, e: Exception?)
            = JadEx(BAD_REQUEST_400, clientMessage, e)

        @JvmStatic
        fun internalError(message: String) = internalError(message, HashMap())

        @JvmStatic
        fun internalError(template: String, params: Map<String, Any>)
            = internalError(TemplateError(template, params))

        @JvmStatic
        fun internalError(messageTemplate: String, param: String, value: Any)
            = internalError(messageTemplate, ImmutableMap.of(param, value))

        @JvmStatic
        fun internalError(error: Error)
            = JadEx(INTERNAL_SERVER_ERROR_500, error)

        @JvmStatic
        fun internalError(clientMessage: String, e: Throwable)
            = JadEx(INTERNAL_SERVER_ERROR_500,
                TemplateError(clientMessage, HashMap()), e)
    }
}
