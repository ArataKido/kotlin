package com.dcop.services

import com.dcop.models.DeliveryOrderPriceResponse
import kotlinx.coroutines.coroutineScope

interface DeliveryOrderPriceService {
    suspend fun calculateDeliveryOrderPrice(
        venueSlug: String,
        cartValue: Int,
        userLat: Double,
        userLon: Double
    ): DeliveryOrderPriceResponse
}
