package com.dcop.utils

import com.dcop.exceptions.InvalidCoordinatesException

class DistanceCalculator(private var strategy: DistanceFunction) {
    fun set_strategy(newStrategy: DistanceFunction) {
        strategy = newStrategy
    }

    fun calculateDistance(userLat: Double, userLon: Double, venueLat: Double, venueLon: Double): Int {
        when {
            userLat !in -90.0..90.0 -> throw InvalidCoordinatesException(
                400,
                "Invalid latitude for user: $userLat. Latitude must be between -90 and 90."
            )

            venueLat !in -90.0..90.0 -> throw InvalidCoordinatesException(
                500,
                "Invalid latitude for venue: $venueLat. Latitude must be between -90 and 90."
            )

            userLon !in -180.0..180.0 -> throw InvalidCoordinatesException(
                400,
                "Invalid longitude for user: $userLon. longitude must be between -180 and 180."
            )

            venueLon !in -180.0..180.0 -> throw InvalidCoordinatesException(
                500,
                "Invalid longitude for venue: $venueLon. longitude must be between -180 and 180."
            )
        }
        return strategy(userLat, userLon, venueLat, venueLon)
    }
}