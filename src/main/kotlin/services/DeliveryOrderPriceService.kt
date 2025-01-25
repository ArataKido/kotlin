package com.dcop.services

import com.dcop.models.DeliveryOrderPriceResponse

interface DeliveryOrderPriceService {
    suspend fun calculateDeliveryOrderPrice(
        venueSlug: String,
        cartValue: Int,
        userLat: Double,
        userLon: Double
    ): DeliveryOrderPriceResponse
}
