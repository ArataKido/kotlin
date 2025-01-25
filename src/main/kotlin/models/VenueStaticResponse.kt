package com.dcop.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VenueStaticResponse(
    @SerialName("venue_raw") val venueRaw: VenueRaw
){
    @Serializable
    data class VenueRaw(
        val location: Location
    ){
        @Serializable
        data class Location(
            val coordinates: List<Double>
        )
    }
}
