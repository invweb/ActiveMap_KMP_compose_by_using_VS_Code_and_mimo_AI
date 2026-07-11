package com.activemap.desktop.service

import com.activemap.shared.service.GeoLocation
import com.activemap.shared.service.LocationService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.URL

@Serializable
private data class IpLocationResponse(
    val status: String = "",
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val city: String = "",
    val country: String = ""
)

class DesktopLocationService : LocationService {
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getCurrentLocation(): GeoLocation? {
        return try {
            val response = URL("http://ip-api.com/json/").readText()
            val parsed = json.decodeFromString<IpLocationResponse>(response)
            if (parsed.status == "success" && parsed.lat != 0.0 && parsed.lon != 0.0) {
                GeoLocation(
                    latitude = parsed.lat,
                    longitude = parsed.lon,
                    accuracy = null
                )
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    override fun getLocationUpdates(intervalMs: Long): Flow<GeoLocation> {
        return emptyFlow()
    }

    override fun hasPermission(): Boolean {
        return true
    }

    override suspend fun requestPermission(): Boolean {
        return true
    }
}
