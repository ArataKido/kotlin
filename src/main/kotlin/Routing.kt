package com.dcop

import io.ktor.server.routing.*
import io.ktor.server.application.*
import com.dcop.routers.deliveryOrderPriceRoutes

fun Application.configureRouting() {
    routing {
        deliveryOrderPriceRoutes()
    }
}
