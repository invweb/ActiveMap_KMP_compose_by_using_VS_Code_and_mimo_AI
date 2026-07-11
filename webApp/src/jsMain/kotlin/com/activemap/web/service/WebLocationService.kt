package com.activemap.web.service

import com.activemap.shared.service.GeoLocation
import com.activemap.shared.service.LocationService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.browser.window
import org.w3c.dom.Geolocation
import org.w3c.dom.GeolocationPosition
import org.w3c.dom.GeolocationPositionError
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.js.Promise

class WebLocationService : LocationService {
    
    private val geolocation: Geolocation? = 
        try {
            window.navigator.geolocation
        } catch (e: Exception) {
            null
        }
    
    override suspend fun getCurrentLocation(): GeoLocation? {
        if (geolocation == null) return null
        if (!hasPermission()) return null
        
        return suspendCoroutine { continuation ->
            geolocation.getCurrentPosition(
                { position: GeolocationPosition ->
                    continuation.resume(
                        GeoLocation(
                            latitude = position.coords.latitude,
                            longitude = position.coords.longitude,
                            accuracy = position.coords.accuracy.toFloat()
                        )
                    )
                },
                { _: GeolocationPositionError ->
                    continuation.resume(null)
                }
            )
        }
    }
    
    override fun getLocationUpdates(intervalMs: Long): Flow<GeoLocation> = callbackFlow {
        if (geolocation == null || !hasPermission()) {
            close()
            return@callbackFlow
        }
        
        val watchId = geolocation.watchPosition(
            { position: GeolocationPosition ->
                trySend(
                    GeoLocation(
                        latitude = position.coords.latitude,
                        longitude = position.coords.longitude,
                        accuracy = position.coords.accuracy.toFloat()
                    )
                )
            },
            { _: GeolocationPositionError ->
                // Error handling
            }
        )
        
        awaitClose {
            geolocation.clearWatch(watchId)
        }
    }
    
    override fun hasPermission(): Boolean {
        return try {
            // In browsers, we can't check permission state synchronously
            // We assume permission is granted if geolocation is available
            geolocation != null
        } catch (e: Exception) {
            false
        }
    }
    
    override suspend fun requestPermission(): Boolean {
        return try {
            if (geolocation == null) return false
            
            suspendCoroutine { continuation ->
                geolocation.getCurrentPosition(
                    { continuation.resume(true) },
                    { continuation.resume(false) }
                )
            }
        } catch (e: Exception) {
            false
        }
    }
}