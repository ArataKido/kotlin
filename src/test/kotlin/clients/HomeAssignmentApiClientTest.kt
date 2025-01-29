package com.dcop.clients

import com.dcop.Config
import com.dcop.exceptions.HttpException
import com.dcop.models.*
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Test class for HomeAssignmentApiClient
 */
class HomeAssignmentApiClientTest {

    private lateinit var client: HttpClient
    private lateinit var apiClient: HomeAssignmentApiClient

    /**
     * Set up the test environment before each test
     */
    @Before
    fun setup() {
        val mockEngine = MockEngine { request ->
            when {
                request.url.encodedPath.endsWith("/static") -> {
                    respond(
                        content = Json.encodeToString(mockStaticResponse),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
                request.url.encodedPath.endsWith("/dynamic") -> {
                    respond(
                        content = Json.encodeToString(mockDynamicResponse),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
                else -> error("Unhandled ${request.url.encodedPath}")
            }
        }

        client = Config.createHttpClient(mockEngine)
        apiClient = HomeAssignmentApiClient(client, Config.apiBaseUrl)
    }

    /**
     * Test fetchVenueData returns correct VenueData for valid venueSlug
     */
    @Test
    fun `fetchVenueData returns correct VenueData for valid venueSlug`() = testApplication {
        runBlocking {
            val result = apiClient.fetchVenueData("test-venue")

            assertEquals(mockStaticResponse, result.static)
            assertEquals(mockDynamicResponse, result.dynamic)
        }
    }

    /**
     * Test fetchVenueData throws HttpException for non-200 response on static data
     */
    @Test
    fun `fetchVenueData throws HttpException for non-200 response on static data`() = testApplication {
        val errorMockEngine = MockEngine { request ->
            when {
                request.url.encodedPath.endsWith("/static") -> {
                    respond(
                        content = "Error",
                        status = HttpStatusCode.InternalServerError,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
                else -> error("Unhandled ${request.url.encodedPath}")
            }
        }

        val errorClient = Config.createHttpClient(errorMockEngine)
        val errorApiClient = HomeAssignmentApiClient(errorClient, Config.apiBaseUrl)

        assertFailsWith<HttpException> {
            runBlocking {
                errorApiClient.fetchVenueData("test-venue")
            }
        }
    }

    /**
     * Test fetchVenueData throws HttpException for non-200 response on dynamic data
     */
    @Test
    fun `fetchVenueData throws HttpException for non-200 response on dynamic data`() = testApplication {
        val errorMockEngine = MockEngine { request ->
            when {
                request.url.encodedPath.endsWith("/dynamic") -> {
                    respond(
                        content = "Error",
                        status = HttpStatusCode.InternalServerError,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
                request.url.encodedPath.endsWith("/static") -> {
                    respond(
                        content = Json.encodeToString(mockStaticResponse),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
                else -> error("Unhandled ${request.url.encodedPath}")
            }
        }

        val errorClient = Config.createHttpClient(errorMockEngine)
        val errorApiClient = HomeAssignmentApiClient(errorClient, Config.apiBaseUrl)

        assertFailsWith<HttpException> {
            runBlocking {
                errorApiClient.fetchVenueData("test-venue")
            }
        }
    }

    /**
     * Test fetchVenueData throws HttpException for network error
     */
    @Test
    fun `fetchVenueData throws HttpException for network error`() = testApplication {
        val errorMockEngine = MockEngine { _ ->
            throw java.io.IOException("Network error")
        }

        val errorClient = Config.createHttpClient(errorMockEngine)
        val errorApiClient = HomeAssignmentApiClient(errorClient, Config.apiBaseUrl)

        assertFailsWith<HttpException> {
            runBlocking {
                errorApiClient.fetchVenueData("test-venue")
            }
        }
    }

    companion object {
        private const val venueLat = 59.3489
        private const val venueLon = 18.0686

        private val mockStaticResponse = VenueStaticResponse(
            staticVenueRaw = StaticVenueRaw(
                location = Location(
                    coordinates = listOf(venueLon, venueLat)
                )
            )
        )

        private val mockDynamicResponse = VenueDynamicResponse(
            dynamicVenueRaw = DynamicVenueRaw(
                deliverySpecs = DeliverySpecs(
                    orderMinimumNoSurcharge = 600,
                    deliveryPricing = DeliveryPricing(
                        basePrice = 100,
                        distanceRanges = listOf(
                            DistanceRange(0, 3000, 20, 1.5)
                        )
                    )
                )
            )
        )
    }
}

