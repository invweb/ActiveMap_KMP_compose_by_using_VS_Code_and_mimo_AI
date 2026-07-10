package com.activemap.shared.service

import com.activemap.shared.model.Route
import com.activemap.shared.model.RoutePoint
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class OsrmService {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    private val baseUrl = "https://router.project-osrm.org"

    suspend fun getRoute(
        startLat: Double,
        startLng: Double,
        endLat: Double,
        endLng: Double
    ): Route {
        val coordinates = "$startLng,$startLat;$endLng,$endLat"
        val response: OsrmResponse = client.get("$baseUrl/route/v1/foot/$coordinates") {
            parameter("overview", "full")
            parameter("geometries", "geojson")
            parameter("steps", "false")
        }.body()

        if (response.code != "Ok" || response.routes.isEmpty()) {
            throw Exception("Route not found: ${response.code}")
        }

        val route = response.routes.first()
        val geometry = route.geometry.coordinates

        return Route(
            startLatitude = startLat,
            startLongitude = startLng,
            endLatitude = endLat,
            endLongitude = endLng,
            points = geometry.map { RoutePoint(latitude = it[1], longitude = it[0]) },
            distanceMeters = route.distance,
            durationSeconds = route.duration
        )
    }

    fun close() {
        client.close()
    }
}

@Serializable
data class OsrmResponse(
    val code: String,
    val routes: List<OsrmRoute>
)

@Serializable
data class OsrmRoute(
    val distance: Double,
    val duration: Double,
    val geometry: OsrmGeometry
)

@Serializable
data class OsrmGeometry(
    val coordinates: List<List<Double>>
)
