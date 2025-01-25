package com.dcop.middleware

import com.dcop.exceptions.HttpException
import com.dcop.exceptions.InvalidCoordinatesException
import com.dcop.exceptions.OutOfRangeException
import com.dcop.models.ExceptionResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

object ExceptionHandler {

    suspend fun handle(
        call: ApplicationCall,
        cause: Throwable,
        developmentMode: Boolean  // In development mode status, 500 has more info in response. Do not use in production.
    ) {
        when (cause) {
            is HttpException -> {
                call.respond(
                    HttpStatusCode.NotFound,
                    ExceptionResponse(cause.message ?: cause.toString(), cause.statusCode)
                )
            }

            is OutOfRangeException -> {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ExceptionResponse(cause.message ?: cause.toString(), cause.statusCode)
                )
            }

            is InvalidCoordinatesException -> {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ExceptionResponse(cause.message ?: cause.toString(), cause.statusCode)
                )
            }

            else -> {
                // All the other Exceptions become status 500, with more info in development mode.
                if (developmentMode) {
                    // Printout stacktrace on console
                    cause.stackTrace.forEach { println(it) }
                    call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
                } else {
                    // We are in production, so only minimal info.
                    call.respondText(text = "Internal Error", status = HttpStatusCode.InternalServerError)
                }
            }


        }
    }
}