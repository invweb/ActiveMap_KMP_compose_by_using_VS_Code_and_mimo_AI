package com.activemap.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class Route(
    val startLatitude: Double,
    val startLongitude: Double,
    val endLatitude: Double,
    val endLongitude: Double,
    val points: List<RoutePoint>,
    val distanceMeters: Double,
    val durationSeconds: Double
) {
    val distanceKm: Double get() = distanceMeters / 1000.0
    val durationMinutes: Double get() = durationSeconds / 60.0
    val durationText: String
        get() {
            val hours = durationSeconds / 3600.0
            return if (hours >= 1.0) {
                "%.1f ч".format(hours)
            } else {
                "%.0f мин".format(durationMinutes)
            }
        }
}

@Serializable
data class RoutePoint(
    val latitude: Double,
    val longitude: Double
)
