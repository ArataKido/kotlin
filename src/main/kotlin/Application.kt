package com.dcop

import com.dcop.middleware.configureLoggingMiddleware
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}


fun Application.module() {
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
//    configureLoggingMiddleware()
    configureHTTP()
    configureSerialization()
//    configureFrameworks()
    configureRouting()
//    configureExceptions()
}
