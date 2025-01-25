package com.dcop.routers

import com.dcop.services.DeliveryOrderPriceService
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import kotlinx.coroutines.*

fun Route.deliveryOrderPriceRoutes() {
    val service by inject<DeliveryOrderPriceService>()
    get("/api/v1/delivery-order-price") {
            val venueSlug = call.parameters["venue_slug"] ?: throw IllegalArgumentException("Missing venue_slug")
            val cartValue = call.parameters["cart_value"]?.toIntOrNull() ?: throw IllegalArgumentException("Invalid cart_value")
            val userLat = call.parameters["user_lat"]?.toDoubleOrNull() ?: throw IllegalArgumentException("Invalid user_lat")
            val userLon = call.parameters["user_lon"]?.toDoubleOrNull() ?: throw IllegalArgumentException("Invalid user_lon")

            val response = service.calculateDeliveryOrderPrice(venueSlug, cartValue, userLat, userLon)
            call.respond(response)
    }
}
