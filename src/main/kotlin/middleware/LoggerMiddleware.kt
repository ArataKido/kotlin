package com.dcop.middleware

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import org.slf4j.LoggerFactory
import kotlin.system.measureTimeMillis

fun Application.configureLoggingMiddleware() {
    val logger = LoggerFactory.getLogger("RequestLogger")

    intercept(ApplicationCallPipeline.Monitoring) {
        val request = call.request
        val method = request.httpMethod.value
        val path = request.uri
        val queryParams = request.queryString()

        logger.info("Received request: $method $path${if (queryParams.isNotBlank()) "?$queryParams" else ""}")

        var status: HttpStatusCode? = null
        val timeTaken = measureTimeMillis {
            proceed()
            status = call.response.status()
        }

        logger.info(
            "Completed request: $method $path - Status: ${status ?: "Unknown"} - Duration: ${timeTaken}ms"
        )
    }
}
