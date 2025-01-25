package com.dcop.utils

import com.dcop.models.DistanceRange

class DeliveryFeeCalculator(private var strategy: PriceFunction) {
    fun set_strategy(newStrategy: PriceFunction) {
        strategy = newStrategy
    }

    fun calculateDeliveryFee(basePrice:Int, distance:Int, distanceRanges: List<DistanceRange>): Int {
        return strategy(basePrice, distance, distanceRanges)
    }
}

