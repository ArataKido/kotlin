package com.dcop.utils

class DistanceCalculator(private var strategy: DistanceFunction){
    fun set_strategy(newStrategy: DistanceFunction){
        strategy = newStrategy
    }

    fun calculateDistance(userLat: Double, userLon: Double, venueLat:Double, venueLon: Double): Int {
//        if ((userLat !in -90.0..90.0) && (venueLat !in -90.0..90.0)) {
//            throw IllegalArgumentException("Latitude must be between -90 and 90.")
//        }
//        if ((userLon !in -180.0..180.0) && (venueLat !in -180.0..180.0)) {
//            throw IllegalArgumentException("Longitude must be between -180 and 180.")
//        }
//      TODO("REWORK THE LOGIC. IF WRONG VENUE COORDITANES, RETURN 500, IF USER RETURN 400")
        when {
            userLat !in -90.0..90.0 -> throw IllegalArgumentException("Invalid latitude for user: $userLat. Latitude must be between -90 and 90.")
            venueLat !in -90.0..90.0 -> throw IllegalArgumentException("Invalid latitude for venue: $venueLat. Latitude must be between -90 and 90.")
            userLon !in -180.0..180.0-> throw IllegalArgumentException("Invalid longitude for venue: $userLon. longitude must be between -180 and 180.")
            venueLon !in -180.0..180.0 -> throw IllegalArgumentException("Invalid longitude for venue: $venueLon. longitude must be between -180 and 180.")
        }
        return strategy(userLat, userLon, venueLat, venueLon)
    }
}