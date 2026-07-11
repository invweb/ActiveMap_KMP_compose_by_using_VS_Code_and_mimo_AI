package com.activemap.shared

import com.activemap.shared.model.*
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LocationModelTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun testLocationSerializationRoundtrip() {
        val location = Location(
            id = "test-id",
            name = "Тестовая локация",
            activityType = ActivityType.SPORT,
            latitude = 55.7558,
            longitude = 37.6173,
            coverage = CoverageLevel.FULL,
            lighting = LightingLevel.BRIGHT,
            inventory = "Скамейка, фонтан",
            cleanliness = CleanlinessLevel.PERFECT,
            noiseLevel = NoiseLevel.QUIET,
            rating = 5,
            status = VisitStatus.WAS_THERE,
            notes = "Отличное место",
            photos = listOf("photo1.jpg", "photo2.jpg")
        )

        val serialized = json.encodeToString(Location.serializer(), location)
        val deserialized = json.decodeFromString(Location.serializer(), serialized)

        assertEquals(location.id, deserialized.id)
        assertEquals(location.name, deserialized.name)
        assertEquals(location.activityType, deserialized.activityType)
        assertEquals(location.latitude, deserialized.latitude)
        assertEquals(location.longitude, deserialized.longitude)
        assertEquals(location.coverage, deserialized.coverage)
        assertEquals(location.lighting, deserialized.lighting)
        assertEquals(location.inventory, deserialized.inventory)
        assertEquals(location.cleanliness, deserialized.cleanliness)
        assertEquals(location.noiseLevel, deserialized.noiseLevel)
        assertEquals(location.rating, deserialized.rating)
        assertEquals(location.status, deserialized.status)
        assertEquals(location.notes, deserialized.notes)
        assertEquals(location.photos, deserialized.photos)
    }

    @Test
    fun testLocationDefaultValues() {
        val location = Location(
            id = "1",
            name = "Тест",
            activityType = ActivityType.WORK,
            latitude = 0.0,
            longitude = 0.0
        )

        assertEquals(CoverageLevel.MEDIUM, location.coverage)
        assertEquals(LightingLevel.MEDIUM, location.lighting)
        assertEquals("", location.inventory)
        assertEquals(CleanlinessLevel.MEDIUM, location.cleanliness)
        assertEquals(NoiseLevel.MEDIUM, location.noiseLevel)
        assertEquals(3, location.rating)
        assertEquals(VisitStatus.WANT_TO_VISIT, location.status)
        assertEquals("", location.notes)
        assertEquals(emptyList(), location.photos)
    }

    @Test
    fun testRouteSerializationRoundtrip() {
        val route = Route(
            startLatitude = 55.75,
            startLongitude = 37.62,
            endLatitude = 55.76,
            endLongitude = 37.63,
            points = listOf(
                RoutePoint(55.75, 37.62),
                RoutePoint(55.755, 37.625),
                RoutePoint(55.76, 37.63)
            ),
            distanceMeters = 1500.0,
            durationSeconds = 1200.0
        )

        val serialized = json.encodeToString(Route.serializer(), route)
        val deserialized = json.decodeFromString(Route.serializer(), serialized)

        assertEquals(route.startLatitude, deserialized.startLatitude)
        assertEquals(route.endLongitude, deserialized.endLongitude)
        assertEquals(3, deserialized.points.size)
        assertEquals(1500.0, deserialized.distanceMeters)
    }

    @Test
    fun testRouteComputedProperties() {
        val route = Route(
            startLatitude = 0.0, startLongitude = 0.0,
            endLatitude = 1.0, endLongitude = 1.0,
            points = emptyList(),
            distanceMeters = 5000.0,
            durationSeconds = 3900.0
        )

        assertEquals(5.0, route.distanceKm)
        assertEquals(65.0, route.durationMinutes)
        assertTrue(route.durationText.contains("ч"))
    }

    @Test
    fun testRouteDurationTextHours() {
        val route = Route(
            startLatitude = 0.0, startLongitude = 0.0,
            endLatitude = 1.0, endLongitude = 1.0,
            points = emptyList(),
            distanceMeters = 10000.0,
            durationSeconds = 7200.0
        )

        assertTrue(route.durationText.contains("ч"))
    }

    @Test
    fun testActivityTypeEnumValues() {
        val values = ActivityType.entries
        assertEquals(5, values.size)
        assertEquals(setOf("SPORT", "WORK", "REST", "EDUCATION", "ENTERTAINMENT"), values.map { it.name }.toSet())
    }

    @Test
    fun testVisitStatusEnumValues() {
        val values = VisitStatus.entries
        assertEquals(3, values.size)
        assertEquals(setOf("WAS_THERE", "WANT_TO_VISIT", "NOT_SUITABLE"), values.map { it.name }.toSet())
    }

    @Test
    fun testLocationFilterDefaultValues() {
        val filter = LocationFilter()
        assertEquals(null, filter.activityType)
        assertEquals(null, filter.status)
        assertEquals("", filter.searchQuery)
    }
}
