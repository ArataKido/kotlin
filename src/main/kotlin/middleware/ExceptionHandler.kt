package com.dcop.middleware

import com.dcop.exceptions.HttpException
import com.dcop.exceptions.InvalidCoordinatesException
import com.dcop.exceptions.OutOfRangeException
import com.dcop.models.ExceptionResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
/**
 * Handles exceptions and sends appropriate HTTP responses.
 */
object ExceptionHandler {
    /**
     * Handles different types of exceptions and responds with appropriate status codes and messages.
     *
     * @param call The ApplicationCall to respond to.
     * @param cause The Throwable that caused the exception.
     * @param developmentMode Whether the application is running in development mode.
     */
    suspend fun handle(
        call: ApplicationCall,
        cause: Throwable,
        developmentMode: Boolean  // In development mode status, 500 has more info in response. Do not use in production.
    ) {
        when (cause) {
            is HttpException -> {
                respondWithException(call, HttpStatusCode.NotFound, cause)
            }

            is OutOfRangeException -> {
                respondWithException(call, HttpStatusCode.BadRequest, cause)
            }

            is InvalidCoordinatesException -> {
                respondWithException(call, HttpStatusCode.InternalServerError, cause)
            }

            else -> handleGenericException(call, cause, developmentMode)
        }
    }

    private suspend fun respondWithException(
        call: ApplicationCall,
        status: HttpStatusCode,
        exception: Throwable
    ) {
        val message = exception.message ?: exception.toString()

        call.respond(
            status,
            ExceptionResponse(message, status.value)
        )
    }

    private suspend fun handleGenericException(
        call: ApplicationCall,
        cause: Throwable,
        developmentMode: Boolean
    ) {
        if (developmentMode) {
            cause.printStackTrace()
            call.respondText(
                text = "500 Internal Server Error: ${cause.localizedMessage}",
                status = HttpStatusCode.InternalServerError
            )
        } else {
            call.respondText(
                text = "Internal Server Error",
                status = HttpStatusCode.InternalServerError
            )
        }
    }
}