package com.activemap.shared.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlin.math.PI

@Serializable
data class LocationPoint(
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val accuracy: Float? = null,
    val speed: Float? = null
) {
    companion object {
        fun fromGeoLocation(geo: com.activemap.shared.service.GeoLocation): LocationPoint {
            return LocationPoint(
                latitude = geo.latitude,
                longitude = geo.longitude,
                timestamp = Clock.System.now().toEpochMilliseconds(),
                accuracy = geo.accuracy
            )
        }
    }
}

@Serializable
data class LocationTrack(
    val id: String,
    val name: String,
    val startDate: Long,
    val endDate: Long? = null,
    val points: List<LocationPoint> = emptyList(),
    val distanceMeters: Double = 0.0,
    val durationMs: Long? = null
) {
    companion object {
        fun startNew(name: String): LocationTrack {
            return LocationTrack(
                id = Clock.System.now().toEpochMilliseconds().toString(),
                name = name,
                startDate = Clock.System.now().toEpochMilliseconds()
            )
        }
    }
    
    fun isActive(): Boolean = endDate == null
    
    fun stop(): LocationTrack {
        val now = Clock.System.now().toEpochMilliseconds()
        return this.copy(
            endDate = now,
            durationMs = now - startDate
        )
    }
    
    fun addPoint(point: LocationPoint): LocationTrack {
        // Recalculate distance if this is a new point
        val newPoints = points + point
        val newDistance = calculateDistance(newPoints)
        return this.copy(points = newPoints, distanceMeters = newDistance)
    }
    
    private fun calculateDistance(points: List<LocationPoint>): Double {
        if (points.size < 2) return 0.0
        
        var totalDistance = 0.0
        for (i in 1 until points.size) {
            totalDistance += haversineDistance(
                points[i - 1].latitude,
                points[i - 1].longitude,
                points[i].latitude,
                points[i].longitude
            )
        }
        return totalDistance
    }
    
    private fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371000.0 // Earth radius in meters
        
        val dLat = (lat2 - lat1) * PI / 180.0
        val dLon = (lon2 - lon1) * PI / 180.0
        
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(lat1 * PI / 180.0) * cos(lat2 * PI / 180.0) *
                sin(dLon / 2) * sin(dLon / 2)
        
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        
        return R * c
    }
    
    private fun sin(x: Double): Double = kotlin.math.sin(x)
    private fun cos(x: Double): Double = kotlin.math.cos(x)
    private fun sqrt(x: Double): Double = kotlin.math.sqrt(x)
    private fun atan2(y: Double, x: Double): Double = kotlin.math.atan2(y, x)
}

@Serializable
enum class TrackStatus {
    ACTIVE,    // Идёт запись
    STOPPED    // Запись остановлена
}
