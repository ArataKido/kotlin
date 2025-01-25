package com.dcop.clients

import com.dcop.exceptions.HttpException
import com.dcop.models.VenueDynamicResponse
import com.dcop.models.VenueStaticResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class HomeAssignmentApiClient(private val client: HttpClient) {
    private val BASE_URL = "https://consumer-api.development.dev.woltapi.com/home-assignment-api/v1/venues/"

    suspend fun getStaticVenueData(venueSlug: String): VenueStaticResponse {
        val response = client.get("$BASE_URL$venueSlug/static")
        if (!response.status.isSuccess()) {
            throw HttpException(response.status.value, "Failed to fetch static data for venueSlug: $venueSlug")
        }
        return response.body()
    }

    suspend fun getDynamicVenueData(venueSlug: String): VenueDynamicResponse {
        val response = client.get("$BASE_URL$venueSlug/dynamic")
        if (response.status.value != 200) {
            throw HttpException(response.status.value, "Failed to fetch static data for venue_slug: {venue_slug}")
        }
        return response.body()
    }
}