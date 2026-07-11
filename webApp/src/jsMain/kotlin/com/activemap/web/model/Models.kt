package com.activemap.web.model

import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val id: String,
    val name: String,
    val activityType: ActivityType,
    val latitude: Double,
    val longitude: Double,
    val coverage: CoverageLevel = CoverageLevel.MEDIUM,
    val lighting: LightingLevel = LightingLevel.MEDIUM,
    val inventory: String = "",
    val cleanliness: CleanlinessLevel = CleanlinessLevel.MEDIUM,
    val noiseLevel: NoiseLevel = NoiseLevel.MEDIUM,
    val rating: Int = 3,
    val status: VisitStatus = VisitStatus.WANT_TO_VISIT,
    val notes: String = "",
    val photos: List<String> = emptyList(),
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)

@Serializable enum class ActivityType { SPORT, WORK, REST, EDUCATION, ENTERTAINMENT }
@Serializable enum class CoverageLevel { NONE, PARTIAL, MEDIUM, FULL }
@Serializable enum class LightingLevel { NONE, LOW, MEDIUM, BRIGHT }
@Serializable enum class CleanlinessLevel { DIRTY, POOR, MEDIUM, CLEAN, PERFECT }
@Serializable enum class NoiseLevel { QUIET, LOW, MEDIUM, LOUD, VERY_LOUD }
@Serializable enum class VisitStatus { WAS_THERE, WANT_TO_VISIT, NOT_SUITABLE }

@Serializable
data class LocationFilter(
    val activityType: ActivityType? = null,
    val status: VisitStatus? = null,
    val searchQuery: String = ""
)

@Serializable
data class Route(
    val startLatitude: Double, val startLongitude: Double,
    val endLatitude: Double, val endLongitude: Double,
    val waypoints: List<RoutePoint> = emptyList(),
    val points: List<RoutePoint>,
    val distanceMeters: Double, val durationSeconds: Double
) {
    val distanceKm: Double get() = distanceMeters / 1000.0
    val durationText: String
        get() {
            val h = durationSeconds / 3600.0
            return if (h >= 1.0) "${(h * 10).toLong() / 10.0} ч" else "${(durationSeconds / 60).toLong()} мин"
        }
}

@Serializable
data class RoutePoint(val latitude: Double, val longitude: Double)
