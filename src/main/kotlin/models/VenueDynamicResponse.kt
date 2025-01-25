package com.dcop.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VenueDynamicResponse(
    @SerialName("venue_raw") val venueRaw: VenueRaw
)

@Serializable
data class VenueRaw(
    @SerialName("delivery_specs") val deliverySpecs: DeliverySpecs
)

@Serializable
data class DeliverySpecs(
    @SerialName("order_minimum_no_surcharge") val orderMinimumNoSurcharge: Int,
    @SerialName("delivery_pricing") val deliveryPricing: DeliveryPricing
)

@Serializable
data class DeliveryPricing(
    @SerialName("base_price") val basePrice: Int,
    @SerialName("distance_ranges") val distanceRanges: List<DistanceRange>
)

@Serializable
data class DistanceRange(
    val min: Int,
    val max: Int,
    val a: Int,
    val b: Double
)
