package com.activemap.shared

import com.activemap.shared.service.OfflineRouteService
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OfflineRouteServiceTest {
    
    private val service = OfflineRouteService()
    
    @Test
    fun testCalculateStraightLineRoute() {
        val route = service.calculateStraightLineRoute(
            startLat = 55.7558,
            startLng = 37.6173,
            endLat = 55.7580,
            endLng = 37.6200
        )
        
        assertEquals(55.7558, route.startLatitude)
        assertEquals(37.6173, route.startLongitude)
        assertEquals(55.7580, route.endLatitude)
        assertEquals(37.6200, route.endLongitude)
        assertTrue(route.distanceMeters > 0)
        assertTrue(route.durationSeconds > 0)
        assertTrue(route.points.size >= 2)
    }
    
    @Test
    fun testRoutePointsAreOnStraightLine() {
        val route = service.calculateStraightLineRoute(
            startLat = 0.0,
            startLng = 0.0,
            endLat = 1.0,
            endLng = 1.0
        )
        
        for (point in route.points) {
            assertTrue(abs(point.latitude - point.longitude) < 0.001)
        }
    }
    
    @Test
    fun testSamePointRoute() {
        val route = service.calculateStraightLineRoute(
            startLat = 55.7558,
            startLng = 37.6173,
            endLat = 55.7558,
            endLng = 37.6173
        )
        
        assertEquals(0.0, route.distanceMeters, 1.0)
        assertTrue(route.points.size >= 2)
    }
    
    @Test
    fun testDistanceCalculation() {
        val route = service.calculateStraightLineRoute(
            startLat = 55.7558,
            startLng = 37.6173,
            endLat = 55.7658,
            endLng = 37.6273
        )
        
        assertTrue(route.distanceMeters > 1000)
        assertTrue(route.distanceMeters < 2000)
    }
}