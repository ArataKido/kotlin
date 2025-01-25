package com.dcop.clients

import com.dcop.exceptions.HttpException
import com.dcop.models.VenueDynamicResponse
import com.dcop.models.VenueStaticResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

class HomeAssignmentApiClient(private val client: HttpClient) {
    private val BASE_URL = "https://consumer-api.development.dev.woltapi.com/home-assignment-api/v1/venues/"

    private suspend fun getStaticVenueData(venueSlug: String): VenueStaticResponse = withContext(Dispatchers.IO) {
        try {
            val response = client.get("$BASE_URL$venueSlug/static")
            if (!response.status.isSuccess()) {
                throw HttpException(response.status.value, "Failed to fetch static data for venueSlug: $venueSlug")
            }
            response.body()
        } catch (e: Exception) {
            throw HttpException(500, "Error fetching static data: ${e.message}")
        }
    }

    private suspend fun getDynamicVenueData(venueSlug: String): VenueDynamicResponse = withContext(Dispatchers.IO) {
        try {
            val response = client.get("$BASE_URL$venueSlug/dynamic")
            if (!response.status.isSuccess()) {
                throw HttpException(response.status.value, "Failed to fetch static data for venue_slug: {venue_slug}")
            }
            response.body()
        } catch (e: Exception) {
            throw HttpException(500, "Error fetching dynamic data: ${e.message}")
        }
    }
    suspend fun fetchVenueData(venueSlug: String) = coroutineScope {
        val staticData = async { getStaticVenueData(venueSlug) }
        val dynamicData = async { getDynamicVenueData(venueSlug) }
        Pair(staticData.await(), dynamicData.await())
    }
}