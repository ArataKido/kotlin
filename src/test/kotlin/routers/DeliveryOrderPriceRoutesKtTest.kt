package com.dcop.routers

import com.dcop.models.Delivery
import com.dcop.models.DeliveryOrderPriceResponse
import com.dcop.services.DeliveryOrderPriceService
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import kotlin.test.assertEquals

class DeliveryOrderPriceRoutesTest : AutoCloseKoinTest() {

    private val mockService = object : DeliveryOrderPriceService {
        override suspend fun calculateDeliveryOrderPrice(
            venueSlug: String,
            cartValue: Int,
            userLat: Double,
            userLon: Double
        ): DeliveryOrderPriceResponse {
            return DeliveryOrderPriceResponse(
                totalPrice = 1500,
                smallOrderSurcharge = 200,
                cartValue = cartValue,
                delivery = Delivery(fee = 300, distance = 5000)
            )
        }
    }

    private fun Application.testModule() {
        install(ContentNegotiation) {
            json()
        }
        routing {
            deliveryOrderPriceRoutes()
        }
    }

    init {
        startKoin {
            modules(module {
                single<DeliveryOrderPriceService> { mockService }
            })
        }
    }

    @Test
    fun `test successful delivery order price calculation`() = testApplication {
        application {
            testModule()
        }

        val response = client.get("/api/v1/delivery-order-price") {
            url {
                parameters.append("venue_slug", "test-venue")
                parameters.append("cart_value", "1000")
                parameters.append("user_lat", "40.7128")
                parameters.append("user_lon", "-74.0060")
            }
        }

        assertEquals(HttpStatusCode.OK, response.status)

        val responseBody = Json.decodeFromString<DeliveryOrderPriceResponse>(response.bodyAsText())
        assertEquals(1500, responseBody.totalPrice)
        assertEquals(200, responseBody.smallOrderSurcharge)
        assertEquals(1000, responseBody.cartValue)
        assertEquals(300, responseBody.delivery.fee)
        assertEquals(5000, responseBody.delivery.distance)
    }

    @Test
    fun `test missing parameters`() = testApplication {
        application {
            testModule()
        }

        val response = client.get("/api/v1/delivery-order-price") {
            url {
                parameters.append("venue_slug", "test-venue")
                // Missing cart_value, user_lat, and user_lon
            }
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)

        val responseBody = response.bodyAsText()
        assert(responseBody.contains("Missing parameters: cart_value, user_lat, user_lon"))
    }

    @Test
    fun `test invalid cart value`() = testApplication {
        application {
            testModule()
        }

        val response = client.get("/api/v1/delivery-order-price") {
            url {
                parameters.append("venue_slug", "test-venue")
                parameters.append("cart_value", "invalid")
                parameters.append("user_lat", "40.7128")
                parameters.append("user_lon", "-74.0060")
            }
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)

        val responseBody = response.bodyAsText()
        assert(responseBody.contains("Missing parameters: cart_value"))
    }

    @Test
    fun `test invalid latitude`() = testApplication {
        application {
            testModule()
        }

        val response = client.get("/api/v1/delivery-order-price") {
            url {
                parameters.append("venue_slug", "test-venue")
                parameters.append("cart_value", "1000")
                parameters.append("user_lat", "invalid")
                parameters.append("user_lon", "-74.0060")
            }
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)

        val responseBody = response.bodyAsText()
        assert(responseBody.contains("Missing parameters: user_lat"))
    }
}