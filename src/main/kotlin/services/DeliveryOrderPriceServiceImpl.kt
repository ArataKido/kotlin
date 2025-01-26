package com.dcop.services

import com.dcop.clients.HomeAssignmentApiClient
import com.dcop.models.*
import com.dcop.utils.DeliveryFeeCalculator
import com.dcop.utils.DistanceCalculator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Implementation of the Delivery Order Price Service.
 * Calculates delivery order prices using the Home Assignment API and utility classes.
 */
class DeliveryOrderPriceServiceImpl(
    private val client: HomeAssignmentApiClient,
    private val distanceCalculator: DistanceCalculator,
    private val deliveryFeeCalculator: DeliveryFeeCalculator
) : DeliveryOrderPriceService {
    private val logger: Logger = LoggerFactory.getLogger("ServiceLogger")

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
            logger.error("Error processing delivery price: ", e)
            throw e
        }
    }

    private fun calculateSmallOrderSurcharge(orderMinimum: Int, cartValue: Int): Int {
        return if (cartValue < orderMinimum) orderMinimum - cartValue else 0
    }
}
