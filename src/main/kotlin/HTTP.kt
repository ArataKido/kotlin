package com.dcop

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.routing.openApiSpec
import io.github.smiley4.ktorswaggerui.routing.swaggerUI
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureHTTP() {
    // Install and configure the OpenAPI plugin
    install(SwaggerUI) {
        info {
            title = "My API"
            version = "1.0.0"
            description = "API documentation for my Ktor application."
        }
        server() {
            url = "http://localhost:8080"
            description = "Local development server"
        }
    }

    // Configure routing
    routing {
        // Create a route for the openapi-spec file.
        route("api.json") {
            openApiSpec()
        }
        // Create a route for the swagger-ui using the openapi-spec at "/api.json".
        route("swagger") {
            swaggerUI("/api.json")
        }
    }
}
