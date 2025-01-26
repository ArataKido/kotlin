package com.dcop

import com.dcop.middleware.configureLoggingMiddleware
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

/**
 * Main application entry point.
 * Configures and starts the Ktor server with the necessary plugins and routes.
 */
fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

/**
 * Configures the application module with necessary plugins and routes.
 */
fun Application.module() {
    configureHTTP()
    configureLoggingMiddleware()
    configureSerialization()
    configureFrameworks()
    configureExceptions()
}
