package com.dcop.utils

import com.dcop.models.DistanceRange

class DeliveryPriceCalculator(private var strategy: PriceFunction) {
    fun set_strategy(newStrategy: PriceFunction) {
        strategy = newStrategy
    }

    fun calculateDeliveryPrice(basePrice:Int, distance:Int, distanceRanges: List<DistanceRange>): Int {
        return strategy(basePrice, distance, distanceRanges)
    }
}

