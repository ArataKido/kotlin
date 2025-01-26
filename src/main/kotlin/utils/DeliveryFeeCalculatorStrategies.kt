package com.dcop.utils

import com.dcop.exceptions.OutOfRangeException
import com.dcop.models.DistanceRange
import kotlin.math.roundToInt

typealias PriceFunction = (basePrice:Int, distance:Int, distanceRanges:List<DistanceRange>) -> Int

object DeliveryFeeCalculatorStrategies {
    val default: PriceFunction = { basePrice: Int,
                                   distance: Int,
                                   distanceRanges: List<DistanceRange> ->

        val applicableRange =
            distanceRanges.find { range -> distance >= range.min && (range.max == 0 || distance < range.max) }
                ?: throw OutOfRangeException(400, "Delivery not possible for the given distance: $distance meters")

        if (applicableRange.max == 0) {
            throw OutOfRangeException(400, "Delivery not possible for the given distance: $distance meters")
        }

        val distanceFee: Int = (applicableRange.b * distance / 10).roundToInt()
        val deliveryFee: Int = basePrice + applicableRange.a + distanceFee

        deliveryFee
    }
}