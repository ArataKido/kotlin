package com.dcop.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
/**
 * VenueRaw.
 *
 * @property staticVenueRaw .
 */
data class VenueStaticResponse(
    @SerialName("venue_raw") val staticVenueRaw: StaticVenueRaw
)


@Serializable
/**
 * Location.
 *
 * @property location .
 */
data class StaticVenueRaw(
    val location: Location
)


@Serializable
/**
 * Coordinates.
 *
 * @property coordinates .
 */
data class Location(
    val coordinates: List<Double>
)

/**
 * Location coordinates.
 *
 * @property latitude .
 * @property longitude
 */
@Serializable
data class Coordinates(
    val latitude: Double,
    val longitude: Double
)