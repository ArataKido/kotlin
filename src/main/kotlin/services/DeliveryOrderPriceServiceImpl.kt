package com.dcop.services

import com.dcop.clients.HomeAssignmentApiClient
import com.dcop.models.DeliveryOrderPriceResponse
import com.dcop.utils.DistanceCalculator
import com.dcop.utils.DistanceCalculatorStrategies
import com.dcop.utils.DeliveryFeeCalculator
import com.dcop.utils.DeliveryFeeCalculatorStrategies
import org.slf4j.LoggerFactory
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope


class DeliveryOrderPriceServiceImpl: DeliveryOrderPriceService{
    private val client = HomeAssignmentApiClient()
    private val distanceCalculator = DistanceCalculator(DistanceCalculatorStrategies.haversine)
    private val deliveryFeeCalculator = DeliveryFeeCalculator(DeliveryFeeCalculatorStrategies.default)
    private val logger = LoggerFactory.getLogger(DeliveryOrderPriceServiceImpl::class.java)

    init {
        logger.info("DeliveryOrderPriceServiceImpl initialized")
    }

    override suspend fun calculateDeliveryOrderPrice(
        venueSlug: String,
        cartValue: Int,
        userLat: Double,
        userLon: Double
    ): DeliveryOrderPriceResponse = coroutineScope{
        val staticDataDeferred = async { client.getStaticVenueData(venueSlug) }
        val dynamicDataDeferred = async { client.getDynamicVenueData(venueSlug)}


        val staticData = staticDataDeferred.await()
        val venueLat: Double = staticData.venueRaw.location.coordinates[1]
        val venueLon: Double = staticData.venueRaw.location.coordinates[0]
        val distance = distanceCalculator.calculateDistance(userLat, userLon, venueLat, venueLon)

        val dynamicData = dynamicDataDeferred.await()

        val basePrice: Int = dynamicData.venueRaw.deliverySpecs.deliveryPricing.basePrice
        val orderMinimumNoSurcharge: Int = dynamicData.venueRaw.deliverySpecs.orderMinimumNoSurcharge
        val distanceRanges = dynamicData.venueRaw.deliverySpecs.deliveryPricing.distanceRanges

        val smallOrderSurcharge = maxOf(0, orderMinimumNoSurcharge - cartValue)
        val deliveryFee = deliveryFeeCalculator.calculateDeliveryFee(basePrice, distance, distanceRanges)
        val totalPrice = deliveryFee + smallOrderSurcharge + cartValue
        val delivery = DeliveryOrderPriceResponse.Delivery(deliveryFee, distance)
        DeliveryOrderPriceResponse(totalPrice, smallOrderSurcharge, cartValue, delivery )
    }


}