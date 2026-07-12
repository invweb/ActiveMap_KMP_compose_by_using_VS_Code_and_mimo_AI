package com.activemap.shared

import com.activemap.shared.model.LocationPoint
import com.activemap.shared.model.LocationTrack
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LocationHistoryModelTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testLocationPointSerializationRoundtrip() {
        val point = LocationPoint(
            latitude = 55.7558,
            longitude = 37.6173,
            timestamp = 1234567890L,
            accuracy = 10.5f,
            speed = 5.0f
        )

        val serialized = json.encodeToString(LocationPoint.serializer(), point)
        val deserialized = json.decodeFromString(LocationPoint.serializer(), serialized)

        assertEquals(point.latitude, deserialized.latitude)
        assertEquals(point.longitude, deserialized.longitude)
        assertEquals(point.timestamp, deserialized.timestamp)
        assertEquals(point.accuracy, deserialized.accuracy)
        assertEquals(point.speed, deserialized.speed)
    }

    @Test
    fun testLocationPointFromGeoLocation() {
        val geo = com.activemap.shared.service.GeoLocation(
            latitude = 55.7558,
            longitude = 37.6173,
            accuracy = 15.0f
        )

        val point = LocationPoint.fromGeoLocation(geo)

        assertEquals(geo.latitude, point.latitude)
        assertEquals(geo.longitude, point.longitude)
        assertEquals(geo.accuracy, point.accuracy)
        // timestamp should be approximately now
        assertTrue(point.timestamp > 0)
    }

    @Test
    fun testLocationTrackCreation() {
        val track = LocationTrack.startNew("Тестовый трек")

        assertEquals("Тестовый трек", track.name)
        assertTrue(track.startDate > 0)
        assertEquals(null, track.endDate)
        assertEquals(emptyList(), track.points)
        assertEquals(0.0, track.distanceMeters)
        assertEquals(null, track.durationMs)
        assertTrue(track.id.isNotEmpty())
    }

    @Test
    fun testLocationTrackStop() {
        val track = LocationTrack.startNew("Тест").copy(
            startDate = 1000000L,
            points = listOf(
                LocationPoint(55.0, 37.0, 1000000L),
                LocationPoint(55.1, 37.1, 2000000L)
            )
        )

        val stopped = track.stop()

        assertTrue(stopped.endDate != null)
        assertEquals(1000000L, stopped.startDate)
        assertTrue(stopped.endDate!! > stopped.startDate)
        assertEquals(stopped.endDate!! - stopped.startDate, stopped.durationMs)
        assertEquals(2, stopped.points.size)
    }

    @Test
    fun testLocationTrackAddPoint() {
        val track = LocationTrack.startNew("Тест")

        val newPoint = LocationPoint(
            latitude = 55.7558,
            longitude = 37.6173,
            timestamp = 1234567890L
        )

        val updatedTrack = track.addPoint(newPoint)

        assertEquals(1, updatedTrack.points.size)
        assertEquals(55.7558, updatedTrack.points[0].latitude)
        assertEquals(37.6173, updatedTrack.points[0].longitude)
    }

    @Test
    fun testLocationTrackHaversineDistance() {
        // Test distance between two points
        val track = LocationTrack.startNew("Тест")

        // Point A: Moscow
        val pointA = LocationPoint(55.7558, 37.6173, 1000000L)
        // Point B: St. Petersburg (approximately 630 km from Moscow)
        val pointB = LocationPoint(59.9311, 30.3609, 2000000L)

        val trackWithPoints = track
            .addPoint(pointA)
            .addPoint(pointB)

        // Distance should be approximately 630 km
        assertTrue(trackWithPoints.distanceMeters > 600000) // At least 600 km
        assertTrue(trackWithPoints.distanceMeters < 700000) // Less than 700 km
    }

    @Test
    fun testLocationTrackActiveStatus() {
        val activeTrack = LocationTrack.startNew("Активный")
        assertTrue(activeTrack.isActive())

        val stoppedTrack = activeTrack.stop()
        assertFalse(stoppedTrack.isActive())
    }

    @Test
    fun testLocationTrackSerializationRoundtrip() {
        val track = LocationTrack.startNew("Тестовый трек").copy(
            points = listOf(
                LocationPoint(55.7558, 37.6173, 1000000L),
                LocationPoint(55.7559, 37.6174, 2000000L)
            ),
            distanceMeters = 150.5,
            durationMs = 1000000L
        )

        val stoppedTrack = track.stop()

        val serialized = json.encodeToString(LocationTrack.serializer(), stoppedTrack)
        val deserialized = json.decodeFromString(LocationTrack.serializer(), serialized)

        assertEquals(stoppedTrack.id, deserialized.id)
        assertEquals(stoppedTrack.name, deserialized.name)
        assertEquals(stoppedTrack.startDate, deserialized.startDate)
        assertEquals(stoppedTrack.endDate, deserialized.endDate)
        assertEquals(stoppedTrack.points.size, deserialized.points.size)
        assertEquals(stoppedTrack.distanceMeters, deserialized.distanceMeters)
        assertEquals(stoppedTrack.durationMs, deserialized.durationMs)
    }

    @Test
    fun testTrackPointDistanceCalculationAccuracy() {
        // Test with known distance: 1 degree of latitude ≈ 111 km
        val track = LocationTrack.startNew("Тест")

        val point1 = LocationPoint(0.0, 0.0, 1000000L)
        val point2 = LocationPoint(1.0, 0.0, 2000000L) // ~111 km away

        val updatedTrack = track.addPoint(point1).addPoint(point2)

        // Should be approximately 111 km
        val distanceKm = updatedTrack.distanceMeters / 1000
        assertTrue(distanceKm > 100) // At least 100 km
        assertTrue(distanceKm < 120) // Less than 120 km
    }

    @Test
    fun testTrackWithNoPoints() {
        val track = LocationTrack.startNew("Пустой")

        assertEquals(0.0, track.distanceMeters)
        assertEquals(emptyList(), track.points)
    }

    @Test
    fun testTrackWithSinglePoint() {
        val track = LocationTrack.startNew("Одна точка")
            .addPoint(LocationPoint(55.0, 37.0, 1000000L))

        assertEquals(0.0, track.distanceMeters) // Single point has zero distance
        assertEquals(1, track.points.size)
    }

    @Test
    fun testMultipleTrackPointsDistance() {
        val track = LocationTrack.startNew("Много точек")

        // Create points in a line, each 1 degree apart
        val points = List(5) { i ->
            LocationPoint(i.toDouble(), 0.0, (i * 1000000).toLong())
        }

        var updatedTrack = track
        points.forEach { point ->
            updatedTrack = updatedTrack.addPoint(point)
        }

        // Should have 5 points
        assertEquals(5, updatedTrack.points.size)

        // Distance should be approximately 4 * 111 km = 444 km
        val distanceKm = updatedTrack.distanceMeters / 1000
        assertTrue(distanceKm > 400)
        assertTrue(distanceKm < 500)
    }
}
