package com.dcop.utils

import kotlin.math.*

typealias DistanceFunction = (Double, Double, Double, Double) -> Int

object DistanceCalculatorStrategies {
    private const val EARTH_RADIUS_METERS = 6371000.0

    val haversine: DistanceFunction = { userLat: Double, userLon: Double, venueLat: Double, venueLon: Double ->
        val deltaLat = Math.toRadians(venueLat - userLat)
        val deltaLon = Math.toRadians(venueLon - userLon)

        val a = sin(deltaLat / 2).pow(2) +
                cos(Math.toRadians(userLat)) * cos(Math.toRadians(venueLat)) * sin(deltaLon / 2).pow(2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        (EARTH_RADIUS_METERS * c).roundToInt()
    }

    val pythagoras: DistanceFunction = { lat1: Double, lon1: Double, lat2: Double, lon2: Double ->
        val deltaLat = Math.toRadians(lat2 - lat1)
        val deltaLon = Math.toRadians(lon2 - lon1)
        val meanLat = Math.toRadians((lat1 + lat2) / 2)

        val x = EARTH_RADIUS_METERS * deltaLon * cos(meanLat)
        val y = EARTH_RADIUS_METERS * deltaLat

        sqrt(x * x + y * y).roundToInt() // Distance in meters
    }
}
