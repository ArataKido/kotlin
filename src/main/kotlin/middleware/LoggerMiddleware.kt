package com.dcop.middleware

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.system.measureTimeMillis

fun Application.configureLoggingMiddleware() {
    val logger: Logger = LoggerFactory.getLogger("RequestLogger")

    intercept(ApplicationCallPipeline.Monitoring) {
        val request = call.request
        val method = request.httpMethod.value
        val path = request.path()
        val queryParams = request.queryString()

        logger.info("Received request: $method $path${if (queryParams.isNotBlank()) "?$queryParams" else ""}")

        var status: HttpStatusCode = HttpStatusCode.OK
        val timeTaken = measureTimeMillis {
            try {
                proceed()
                status = call.response.status() ?: HttpStatusCode.OK
            } catch (e: Exception) {
                status = HttpStatusCode.InternalServerError
                logger.error("Error processing request: $method $path", e)
                throw e
            }
        }

        logger.info("Completed request: $method $path - Status: $status - Duration: ${timeTaken}ms")
    }
}
