package com.dcop.services

import com.dcop.clients.HomeAssignmentApiClient
import com.dcop.models.DeliveryOrderPriceResponse
import com.dcop.utils.DeliveryFeeCalculator
import com.dcop.utils.DistanceCalculator
import kotlinx.coroutines.coroutineScope


class DeliveryOrderPriceServiceImpl(
    private val client: HomeAssignmentApiClient,
    private val distanceCalculator: DistanceCalculator,
    private val deliveryFeeCalculator: DeliveryFeeCalculator
) : DeliveryOrderPriceService {


    override suspend fun calculateDeliveryOrderPrice(
        venueSlug: String,
        cartValue: Int,
        userLat: Double,
        userLon: Double
    ): DeliveryOrderPriceResponse = coroutineScope {
        val clientResponse = client.fetchVenueData(venueSlug)
        val staticData = clientResponse.first
        val dynamicData = clientResponse.second

        val venueLat: Double = staticData.venueRaw.location.coordinates[1]
        val venueLon: Double = staticData.venueRaw.location.coordinates[0]
        val distance = distanceCalculator.calculateDistance(userLat, userLon, venueLat, venueLon)


        val basePrice: Int = dynamicData.venueRaw.deliverySpecs.deliveryPricing.basePrice
        val orderMinimumNoSurcharge: Int = dynamicData.venueRaw.deliverySpecs.orderMinimumNoSurcharge
        val distanceRanges = dynamicData.venueRaw.deliverySpecs.deliveryPricing.distanceRanges

        val smallOrderSurcharge = maxOf(0, orderMinimumNoSurcharge - cartValue)
        val deliveryFee = deliveryFeeCalculator.calculateDeliveryFee(basePrice, distance, distanceRanges)
        val totalPrice = deliveryFee + smallOrderSurcharge + cartValue
        val delivery = DeliveryOrderPriceResponse.Delivery(deliveryFee, distance)
        DeliveryOrderPriceResponse(totalPrice, smallOrderSurcharge, cartValue, delivery)
    }


}