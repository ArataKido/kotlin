package com.dcop

import com.dcop.middleware.configureLoggingMiddleware
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}


fun Application.module() {
    configureLoggingMiddleware()
    configureSerialization()
    configureFrameworks()
    configureRouting()
    configureExceptions()
    configureHTTP()
}
