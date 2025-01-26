package com.dcop.services

import com.dcop.clients.HomeAssignmentApiClient
import com.dcop.models.*
import com.dcop.utils.DeliveryFeeCalculator
import com.dcop.utils.DistanceCalculator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
    ): DeliveryOrderPriceResponse = withContext(Dispatchers.Default) {
        try {
            val (staticData: VenueStaticResponse, dynamicData: VenueDynamicResponse) = client.fetchVenueData(venueSlug)

            val (venueLon: Double, venueLat: Double) = staticData.staticVenueRaw.location.coordinates
            val distance: Int = distanceCalculator.calculateDistance(userLat, userLon, venueLat, venueLon)

            val deliverySpecs: DeliverySpecs = dynamicData.dynamicVenueRaw.deliverySpecs
            val deliveryPricing: DeliveryPricing = deliverySpecs.deliveryPricing

            val basePrice: Int = deliveryPricing.basePrice
            val distanceRanges: List<DistanceRange> = deliveryPricing.distanceRanges
            val orderMinimumNoSurcharge: Int = deliverySpecs.orderMinimumNoSurcharge

            val smallOrderSurcharge: Int = calculateSmallOrderSurcharge(orderMinimumNoSurcharge, cartValue)

            val deliveryFee: Int = deliveryFeeCalculator.calculateDeliveryFee(basePrice, distance, distanceRanges)
            val totalPrice: Int = deliveryFee + smallOrderSurcharge + cartValue

            val delivery = Delivery(deliveryFee, distance)
            DeliveryOrderPriceResponse(totalPrice, smallOrderSurcharge, cartValue, delivery)
        } catch (e: Exception) {
            // Бла-бла-бла
            throw e
        }
    }

    private fun calculateSmallOrderSurcharge(orderMinimum: Int, cartValue: Int): Int {
        return if (cartValue < orderMinimum) orderMinimum - cartValue else 0
    }
}

//// Чето там на исключенском
//class DeliveryPriceCalculationException(message: String, cause: Throwable) : Exception(message, cause)

//class DeliveryOrderPriceServiceImpl(
//    private val client: HomeAssignmentApiClient,
//    private val distanceCalculator: DistanceCalculator,
//    private val deliveryFeeCalculator: DeliveryFeeCalculator
//) : DeliveryOrderPriceService {
//    override suspend fun calculateDeliveryOrderPrice(
//        venueSlug: String,
//        cartValue: Int,
//        userLat: Double,
//        userLon: Double
//    ): DeliveryOrderPriceResponse = coroutineScope {
//        val (staticData, dynamicData) = client.fetchVenueData(venueSlug)
//
//        val venueLat: Double = staticData.staticVenueRaw.location.coordinates.latitude
//        val venueLon: Double = staticData.staticVenueRaw.location.coordinates.longitude
//        val distance = distanceCalculator.calculateDistance(userLat, userLon, venueLat, venueLon)
//
//        val basePrice: Int = dynamicData.dynamicVenueRaw.deliverySpecs.deliveryPricing.basePrice
//        val orderMinimumNoSurcharge: Int = dynamicData.dynamicVenueRaw.deliverySpecs.orderMinimumNoSurcharge
//        val distanceRanges = dynamicData.dynamicVenueRaw.deliverySpecs.deliveryPricing.distanceRanges
//
//        val smallOrderSurcharge = maxOf(0, orderMinimumNoSurcharge - cartValue)
//        val deliveryFee = deliveryFeeCalculator.calculateDeliveryFee(basePrice, distance, distanceRanges)
//        val totalPrice = deliveryFee + smallOrderSurcharge + cartValue
//        val delivery = Delivery(deliveryFee, distance)
//        DeliveryOrderPriceResponse(totalPrice, smallOrderSurcharge, cartValue, delivery)
//    }
//
//}