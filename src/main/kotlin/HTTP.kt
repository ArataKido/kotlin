package com.dcop

import com.dcop.routers.deliveryOrderPriceRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.swagger.*


fun Application.configureHTTP() {

    routing {

        swaggerUI(path = "swagger" ) {
            version = "4.15.5"
        }
        deliveryOrderPriceRoutes()
    }
}
