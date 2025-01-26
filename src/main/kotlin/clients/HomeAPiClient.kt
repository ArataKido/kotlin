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

/**
 * Client for interacting with the Home Assignment API.
 * Fetches static and dynamic venue information.
 */
class HomeAssignmentApiClient(private val client: HttpClient, val BASE_URL:String) {

    /**
     * Generic method for fetching data from API
     *
     * @param endpoint The endpoint where the reqeust must be sent
     * @param errorMessage The error message which will be returned in case of unsuccessful request
     * @return The .
     */
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

    /**
     * Fetches static venue information.
     *
     * @param venueSlug The unique identifier for the venue.
     * @return The static venue information.
     */
    private suspend fun getStaticVenueData(venueSlug: String): VenueStaticResponse {
        return fetchData(
            endpoint = "$venueSlug/static",
            errorMessage = "Failed to fetch static data for venueSlug: $venueSlug"
        )
    }

    /**
     * Fetches dynamic venue information.
     *
     * @param venueSlug The unique identifier for the venue.
     * @return The dynamic venue information.
     */
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