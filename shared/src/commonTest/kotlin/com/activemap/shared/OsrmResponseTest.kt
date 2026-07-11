package com.activemap.shared

import com.activemap.shared.service.OsrmGeometry
import com.activemap.shared.service.OsrmResponse
import com.activemap.shared.service.OsrmRoute
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OsrmResponseTest {

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    @Test
    fun testParseOsrmResponse() {
        val jsonStr = """
            {
                "code": "Ok",
                "routes": [{
                    "distance": 1500.5,
                    "duration": 1200.3,
                    "geometry": {
                        "coordinates": [[37.6173, 55.7558], [37.6200, 55.7580], [37.6250, 55.7600]]
                    }
                }]
            }
        """.trimIndent()

        val response = json.decodeFromString<OsrmResponse>(jsonStr)

        assertEquals("Ok", response.code)
        assertEquals(1, response.routes.size)
        assertEquals(1500.5, response.routes[0].distance)
        assertEquals(1200.3, response.routes[0].duration)
        assertEquals(3, response.routes[0].geometry.coordinates.size)
    }

    @Test
    fun testParseOsrmCoordinatesAreLngLat() {
        val jsonStr = """
            {
                "code": "Ok",
                "routes": [{
                    "distance": 100.0,
                    "duration": 60.0,
                    "geometry": {
                        "coordinates": [[37.6173, 55.7558]]
                    }
                }]
            }
        """.trimIndent()

        val response = json.decodeFromString<OsrmResponse>(jsonStr)
        val coord = response.routes[0].geometry.coordinates[0]

        assertEquals(37.6173, coord[0])
        assertEquals(55.7558, coord[1])
    }

    @Test
    fun testParseOsrmErrorResponse() {
        val jsonStr = """{"code": "NoRoute", "routes": []}"""
        val response = json.decodeFromString<OsrmResponse>(jsonStr)

        assertEquals("NoRoute", response.code)
        assertTrue(response.routes.isEmpty())
    }

    @Test
    fun testParseOsrmMultipleRoutes() {
        val jsonStr = """
            {
                "code": "Ok",
                "routes": [
                    {
                        "distance": 1000.0,
                        "duration": 800.0,
                        "geometry": {"coordinates": [[37.0, 55.0], [37.1, 55.1]]}
                    },
                    {
                        "distance": 2000.0,
                        "duration": 1500.0,
                        "geometry": {"coordinates": [[37.0, 55.0], [37.2, 55.2]]}
                    }
                ]
            }
        """.trimIndent()

        val response = json.decodeFromString<OsrmResponse>(jsonStr)

        assertEquals(2, response.routes.size)
        assertEquals(1000.0, response.routes[0].distance)
        assertEquals(2000.0, response.routes[1].distance)
    }

    @Test
    fun testRoutePointSwapFromLngLat() {
        val geometry = OsrmGeometry(
            coordinates = listOf(
                listOf(37.6173, 55.7558),
                listOf(37.6200, 55.7580)
            )
        )

        val points = geometry.coordinates.map { coord ->
            com.activemap.shared.model.RoutePoint(
                latitude = coord[1],
                longitude = coord[0]
            )
        }

        assertEquals(55.7558, points[0].latitude)
        assertEquals(37.6173, points[0].longitude)
        assertEquals(55.7580, points[1].latitude)
        assertEquals(37.6200, points[1].longitude)
    }
}
