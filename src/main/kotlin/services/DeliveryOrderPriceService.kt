package com.dcop.services

import com.dcop.models.DeliveryOrderPriceResponse

/**
 * Interface for the Delivery Order Price Service.
 * Defines the contract for calculating delivery order prices.
 */
interface DeliveryOrderPriceService {
    /**
     * Calculates the delivery order price based on the given request.
     *
     * @param venueSlug TThe unique identifier (slug) for the venue from which the delivery order will be placed
     * @param cartValue The total value of the items in the shopping cart
     * @param userLat The users latitude location
     * @param userLon The users longitude location
     * @return The calculated delivery order price response.
     */
    suspend fun calculateDeliveryOrderPrice(
        venueSlug: String,
        cartValue: Int,
        userLat: Double,
        userLon: Double
    ): DeliveryOrderPriceResponse
}
