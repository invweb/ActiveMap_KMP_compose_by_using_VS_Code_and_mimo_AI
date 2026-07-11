package com.activemap.desktop.service

import com.activemap.shared.service.GeoLocation
import com.activemap.shared.service.LocationService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class DesktopLocationService : LocationService {
    
    override suspend fun getCurrentLocation(): GeoLocation? {
        // Desktop doesn't have built-in geolocation
        // Could be extended with IP-based geolocation
        return null
    }
    
    override fun getLocationUpdates(intervalMs: Long): Flow<GeoLocation> {
        return emptyFlow()
    }
    
    override fun hasPermission(): Boolean {
        return true // No permission needed for desktop
    }
    
    override suspend fun requestPermission(): Boolean {
        return true
    }
}