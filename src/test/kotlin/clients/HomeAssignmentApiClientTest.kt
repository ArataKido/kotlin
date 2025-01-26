//package com.dcop.clients
//
//import com.dcop.exceptions.HttpException
//import com.dcop.models.*
//import io.ktor.client.*
//import io.ktor.client.engine.mock.*
//import io.ktor.client.plugins.contentnegotiation.*
//import io.ktor.http.*
//import io.ktor.serialization.kotlinx.json.*
//import io.ktor.server.testing.*
//import kotlinx.coroutines.runBlocking
//import kotlinx.serialization.encodeToString
//import kotlinx.serialization.json.Json
//import org.junit.Before
//import org.junit.Test
//import kotlin.test.assertEquals
//import kotlin.test.assertFailsWith
//
//class HomeAssignmentApiClientTest {
//
//    private lateinit var mockEngine: MockEngine
//    private lateinit var client: HttpClient
//    private lateinit var apiClient: HomeAssignmentApiClient
//
//    @Before
//    fun setup() {
//        mockEngine = MockEngine { request ->
//            when {
//                request.url.encodedPath.endsWith("/static") -> {
//                    respond(
//                        content = Json.encodeToString(mockStaticResponse),
//                        status = HttpStatusCode.OK,
//                        headers = headersOf(HttpHeaders.ContentType, "application/json")
//                    )
//                }
//                request.url.encodedPath.endsWith("/dynamic") -> {
//                    respond(
//                        content = Json.encodeToString(mockDynamicResponse),
//                        status = HttpStatusCode.OK,
//                        headers = headersOf(HttpHeaders.ContentType, "application/json")
//                    )
//                }
//                else -> error("Unhandled ${request.url.encodedPath}")
//            }
//        }
//
//        client = HttpClient(mockEngine) {
//            install(ContentNegotiation) {
//                json()
//            }
//        }
//
//        apiClient = HomeAssignmentApiClient(client)
//    }
//
//    @Test
//    fun `fetchVenueData returns correct VenueData for valid venueSlug`() = testApplication {
//        runBlocking {
//            val result = apiClient.fetchVenueData("test-venue")
//
//            assertEquals(mockStaticResponse, result.static)
//            assertEquals(mockDynamicResponse, result.dynamic)
//        }
//    }
//
//    @Test
//    fun `fetchVenueData throws HttpException for non-200 response on static data`() = testApplication {
//        mockEngine.addHandler { request ->
//            when {
//                request.url.encodedPath.endsWith("/static") -> {
//                    respond(
//                        content = "Error",
//                        status = HttpStatusCode.InternalServerError,
//                        headers = headersOf(HttpHeaders.ContentType, "application/json")
//                    )
//                }
//                else -> error("Unhandled ${request.url.encodedPath}")
//            }
//        }
//
//        assertFailsWith<HttpException> {
//            runBlocking {
//                apiClient.fetchVenueData("test-venue")
//            }
//        }
//    }
//
//    @Test
//    fun `fetchVenueData throws HttpException for non-200 response on dynamic data`() = testApplication {
//        mockEngine.addHandler { request ->
//            when {
//                request.url.encodedPath.endsWith("/dynamic") -> {
//                    respond(
//                        content = "Error",
//                        status = HttpStatusCode.InternalServerError,
//                        headers = headersOf(HttpHeaders.ContentType, "application/json")
//                    )
//                }
//                else -> error("Unhandled ${request.url.encodedPath}")
//            }
//        }
//
//        assertFailsWith<HttpException> {
//            runBlocking {
//                apiClient.fetchVenueData("test-venue")
//            }
//        }
//    }
//
//    @Test
//    fun `fetchVenueData throws HttpException for network error`() = testApplication {
//        mockEngine.addHandler { _ ->
//            throw java.io.IOException("Network error")
//        }
//
//        assertFailsWith<HttpException> {
//            runBlocking {
//                apiClient.fetchVenueData("test-venue")
//            }
//        }
//    }
//
//    companion object {
//        private val mockStaticResponse = VenueStaticResponse(
//            id = "test-id",
//            name = "Test Venue",
//            image = "https://example.com/image.jpg",
//            location = Location(1.0, 2.0)
//        )
//
//        private val mockDynamicResponse = VenueDynamicResponse(
//            id = "test-id",
//            deliveryPrice = 500,
//            estimatedDeliveryTime = 30
//        )
//    }
//}