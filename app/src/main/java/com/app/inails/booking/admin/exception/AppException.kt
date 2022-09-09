package com.app.inails.booking.admin.exception

import com.app.inails.booking.admin.model.api.ApiError
import com.google.gson.Gson

class InternalServerException : RuntimeException("Error internal server")
class ServerResponseNullException : RuntimeException("Server response no Content")
class ParameterInvalidException(message: String) : RuntimeException(message)

open class ApiRequestException(rawMessage: String?) : RuntimeException() {
    private val mMessage = rawMessage
        ?.let {
            try {
                Gson().fromJson(it, ApiError::class.java)
            } catch (e: Throwable) {
                ApiError(0, rawMessage, null, mutableListOf())
            }
        }
        ?.takeIf { it.body.isNotEmpty() }?.body ?: "Unknown"

    override val message: String
        get() = mMessage
}
