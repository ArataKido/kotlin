package com.dcop.services

import com.dcop.clients.HomeAssignmentApiClient
import com.dcop.exceptions.OutOfRangeException
import com.dcop.models.DeliveryOrderPriceResponse
import com.dcop.models.DistanceRange
import com.dcop.utils.DistanceCalculator
import com.dcop.utils.DistanceCalculatorStrategies
import org.slf4j.LoggerFactory
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.math.roundToInt


class DeliveryOrderPriceServiceImpl: DeliveryOrderPriceService{
    private val client = HomeAssignmentApiClient()
    private val calculator = DistanceCalculator(DistanceCalculatorStrategies.haversine)
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
        val distance = calculator.calculateDistance(userLat, userLon, venueLat, venueLon)

        val dynamicData = dynamicDataDeferred.await()

        val basePrice: Int = dynamicData.venueRaw.deliverySpecs.deliveryPricing.basePrice
        val orderMinimumNoSurcharge: Int = dynamicData.venueRaw.deliverySpecs.orderMinimumNoSurcharge
        val distanceRanges = dynamicData.venueRaw.deliverySpecs.deliveryPricing.distanceRanges

        val smallOrderSurcharge = maxOf(0, orderMinimumNoSurcharge - cartValue)
        val deliveryFee = calculateDeliveryPrice(basePrice, distance, distanceRanges)
        val totalPrice = deliveryFee + smallOrderSurcharge + cartValue
        val delivery = DeliveryOrderPriceResponse.Delivery(deliveryFee, distance)
        DeliveryOrderPriceResponse(totalPrice, smallOrderSurcharge, cartValue, delivery )
    }

    fun calculateDeliveryPrice(
        basePrice: Int,
        distance: Int,
        distanceRanges: List<DistanceRange>
    ): Int {

        // Find the applicable distance range
        val applicableRange = distanceRanges.find { range ->
            distance >= range.min && (range.max == 0 || distance < range.max)
        } ?: throw OutOfRangeException(400, "Delivery not possible for the given distance: $distance meters")

        // If max == 0 in the range, it means delivery is not possible
        if (applicableRange.max == 0) {
            throw OutOfRangeException(400, "Delivery not possible for the given distance: $distance meters")
        }

        // Calculate delivery fee
        val distanceFee = (applicableRange.b * distance / 10).roundToInt()
        val deliveryFee = basePrice + applicableRange.a + distanceFee

        // Calculate total price
        return deliveryFee
    }
}