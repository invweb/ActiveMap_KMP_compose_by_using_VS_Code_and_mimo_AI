package com.activemap.shared.service

import kotlinx.coroutines.flow.Flow

data class GeoLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float? = null
)

interface LocationService {
    suspend fun getCurrentLocation(): GeoLocation?
    fun getLocationUpdates(intervalMs: Long = 1000L): Flow<GeoLocation>
    fun hasPermission(): Boolean
    suspend fun requestPermission(): Boolean
}