package com.dcop

import com.dcop.routers.deliveryOrderPriceRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.swagger.*
import io.swagger.codegen.v3.generators.html.*

fun Application.configureHTTP() {

    routing {

        openAPI(path="openapi", ) {
            codegen = StaticHtmlCodegen()
        }
        swaggerUI(path = "swagger", ) {
            version = "4.15.5"
        }
        deliveryOrderPriceRoutes()

    }
}
