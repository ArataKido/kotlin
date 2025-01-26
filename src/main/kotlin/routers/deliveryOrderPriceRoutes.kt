package com.dcop.routers

import com.dcop.models.DeliveryOrderPriceResponse
import com.dcop.services.DeliveryOrderPriceService
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.deliveryOrderPriceRoutes() {
    val service by inject<DeliveryOrderPriceService>()

    route("/api/v1") {
        get("/delivery-order-price") {
            val venueSlug = call.request.queryParameters["venue_slug"]
            val cartValue = call.request.queryParameters["cart_value"]?.toIntOrNull()

            val userLat = call.request.queryParameters["user_lat"]?.toDoubleOrNull()
            val userLon = call.request.queryParameters["user_lon"]?.toDoubleOrNull()

            val missingParams = listOfNotNull(
                if (venueSlug == null) "venue_slug" else null,
                if (cartValue == null) "cart_value" else null,
                if (userLat == null) "user_lat" else null,
                if (userLon == null) "user_lon" else null
            )

            if (missingParams.isNotEmpty()) {
                val message = "Missing parameters: ${missingParams.joinToString(", ")}"
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to message)
                )
                return@get
            }

            val response: DeliveryOrderPriceResponse = service.calculateDeliveryOrderPrice(
                venueSlug!!,
                cartValue!!,
                userLat!!,
                userLon!!
            )
            call.respond(response)
        }
    }
}