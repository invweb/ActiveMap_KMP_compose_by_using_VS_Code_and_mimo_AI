package com.activemap.shared.service

import com.activemap.shared.model.Route
import com.activemap.shared.model.RoutePoint
import kotlin.math.*

class OfflineRouteService {
    
    private fun toRadians(degrees: Double): Double = degrees * PI / 180.0
    
    fun calculateStraightLineRoute(
        startLat: Double,
        startLng: Double,
        endLat: Double,
        endLng: Double
    ): Route {
        val distance = calculateDistance(startLat, startLng, endLat, endLng)
        val duration = estimateWalkingDuration(distance)
        
        val points = generateStraightLinePoints(startLat, startLng, endLat, endLng)
        
        return Route(
            startLatitude = startLat,
            startLongitude = startLng,
            endLatitude = endLat,
            endLongitude = endLng,
            points = points,
            distanceMeters = distance,
            durationSeconds = duration
        )
    }
    
    private fun calculateDistance(
        startLat: Double,
        startLng: Double,
        endLat: Double,
        endLng: Double
    ): Double {
        val earthRadius = 6371000.0
        
        val dLat = toRadians(endLat - startLat)
        val dLng = toRadians(endLng - startLng)
        
        val a = sin(dLat / 2).pow(2) +
                cos(toRadians(startLat)) * cos(toRadians(endLat)) *
                sin(dLng / 2).pow(2)
        
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        
        return earthRadius * c
    }
    
    private fun estimateWalkingDuration(distanceMeters: Double): Double {
        val averageWalkingSpeed = 1.4
        return distanceMeters / averageWalkingSpeed
    }
    
    private fun generateStraightLinePoints(
        startLat: Double,
        startLng: Double,
        endLat: Double,
        endLng: Double,
        numPoints: Int = 20
    ): List<RoutePoint> {
        val points = mutableListOf<RoutePoint>()
        
        for (i in 0..numPoints) {
            val fraction = i.toDouble() / numPoints
            val lat = startLat + (endLat - startLat) * fraction
            val lng = startLng + (endLng - startLng) * fraction
            points.add(RoutePoint(latitude = lat, longitude = lng))
        }
        
        return points
    }
}