package com.dcop.utils

import com.dcop.exceptions.InvalidCoordinatesException

class DistanceCalculator(private var strategy: DistanceFunction) {
    fun setStrategy(newStrategy: DistanceFunction) {
        strategy = newStrategy
    }
    /**
     * Calculates the distance between two points.
     *
     * @param userLat Latitude of the user.
     * @param userLon Longitude of the user.
     * @param venueLat Latitude of the venue.
     * @param venueLon Longitude of the venue.
     * @return The calculated distance in meters.
     */
    fun calculateDistance(
        userLat: Double,
        userLon: Double,
        venueLat: Double,
        venueLon: Double
    ): Int {
        validateLatitude(userLat, "user")
        validateLatitude(venueLat, "venue")
        validateLongitude(userLon, "user")
        validateLongitude(venueLon, "venue")

        return strategy(userLat, userLon, venueLat, venueLon)
    }


    private fun validateLatitude(lat: Double, entity: String) {
        require(lat in -90.0..90.0) {
            InvalidCoordinatesException(
                400, "Invalid latitude for $entity $lat. Latitude must be between -90 and 90."
            )
        }
    }

    private fun validateLongitude(lon: Double, entity: String) {
        require(lon in -180.0..180.0) {
            InvalidCoordinatesException(
                400, "Invalid longitude for $entity: $lon. Longitude must be between -180 and 180."
            )
        }
    }
}