package com.dcop

import com.dcop.middleware.ExceptionHandler
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*

fun Application.configureExceptions() {
//    val developmentMode = isDevelopmentMode()
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            ExceptionHandler.handle(call, cause, true)
        }
    }
}