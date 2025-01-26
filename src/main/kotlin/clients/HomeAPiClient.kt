package com.dcop.clients

import com.dcop.exceptions.HttpException
import com.dcop.models.VenueData
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
    val BASE_URL = "https://consumer-api.development.dev.woltapi.com/home-assignment-api/v1/venues/"

    private suspend inline fun <reified T> fetchData(
        endpoint: String,
        errorMessage: String
    ): T = withContext(Dispatchers.IO) {
        try {
            val response = client.get("$BASE_URL$endpoint")

            if (!response.status.isSuccess()) {
                throw HttpException(response.status.value, errorMessage)
            }

            response.body()
        } catch (e: Exception) {
            throw HttpException(500, "$errorMessage: ${e.message}")
        }
    }

    private suspend fun getStaticVenueData(venueSlug: String): VenueStaticResponse {
        return fetchData(
            endpoint = "$venueSlug/static",
            errorMessage = "Failed to fetch static data for venueSlug: $venueSlug"
        )
    }

    private suspend fun getDynamicVenueData(venueSlug: String): VenueDynamicResponse {
        return fetchData(
            endpoint = "$venueSlug/dynamic",
            errorMessage = "Failed to fetch dynamic data for venueSlug: $venueSlug"
        )
    }

    suspend fun fetchVenueData(venueSlug: String): VenueData = coroutineScope {
        val staticData = async { getStaticVenueData(venueSlug) }
        val dynamicData = async { getDynamicVenueData(venueSlug) }
        VenueData(staticData.await(), dynamicData.await())
    }
}