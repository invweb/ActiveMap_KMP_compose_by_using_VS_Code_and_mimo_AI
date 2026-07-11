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
        return calculateMultiPointRoute(listOf(
            RoutePoint(startLat, startLng),
            RoutePoint(endLat, endLng)
        ))
    }

    fun calculateMultiPointRoute(waypoints: List<RoutePoint>): Route {
        require(waypoints.size >= 2) { "Need at least 2 waypoints" }

        val allPoints = mutableListOf<RoutePoint>()
        var totalDistance = 0.0

        for (i in 0 until waypoints.size - 1) {
            val from = waypoints[i]
            val to = waypoints[i + 1]
            totalDistance += calculateDistance(from.latitude, from.longitude, to.latitude, to.longitude)

            val segmentPoints = generateStraightLinePoints(
                from.latitude, from.longitude,
                to.latitude, to.longitude,
                numPoints = 10
            )
            if (i > 0) segmentPoints.drop(1)
            allPoints.addAll(segmentPoints)
        }

        val duration = estimateWalkingDuration(totalDistance)

        return Route(
            startLatitude = waypoints.first().latitude,
            startLongitude = waypoints.first().longitude,
            endLatitude = waypoints.last().latitude,
            endLongitude = waypoints.last().longitude,
            waypoints = waypoints,
            points = allPoints,
            distanceMeters = totalDistance,
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
