package com.dcop.utils

import com.dcop.exceptions.OutOfRangeException
import com.dcop.models.DistanceRange
import kotlin.math.*

typealias PriceFunction = (Int, Int, List<DistanceRange>) -> Int

object DeliveryPriceCalculatorStrategies {

    val default: PriceFunction = {
        basePrice: Int,
        distance: Int,
        distanceRanges: List<DistanceRange> ->

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
        deliveryFee
    }
}