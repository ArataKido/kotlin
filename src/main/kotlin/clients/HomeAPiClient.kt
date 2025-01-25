package com.dcop.clients

//import VenueDynamicResponse
import com.dcop.exceptions.HttpException
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

import com.dcop.models.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.http.*

class HomeAssignmentApiClient {
    private val BASE_URL = "https://consumer-api.development.dev.woltapi.com/home-assignment-api/v1/venues/"
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        install(HttpRequestRetry){
            retryOnServerErrors(maxRetries = 3)
            exponentialDelay()
        }
    }


    suspend fun getStaticVenueData(venueSlug: String): VenueStaticResponse {
        val response = client.get("$BASE_URL$venueSlug/static")
        if (!response.status.isSuccess()){
            throw HttpException(response.status.value,"Failed to fetch static data for venue_slug: {venue_slug}")
        }
        return response.body()
    }

    suspend fun getDynamicVenueData(venueSlug: String): VenueDynamicResponse {
        val response = client.get("$BASE_URL$venueSlug/dynamic")
        if (response.status.value != 200){
            throw HttpException(response.status.value, "Failed to fetch static data for venue_slug: {venue_slug}")
        }
        return response.body()
    }
}