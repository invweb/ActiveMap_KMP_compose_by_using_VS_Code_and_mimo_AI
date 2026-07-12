package com.activemap.shared.repository

import com.activemap.shared.model.Location
import com.activemap.shared.model.LocationFilter
import com.activemap.shared.model.LocationTrack
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun getAllLocations(): Flow<List<Location>>
    fun getFilteredLocations(filter: LocationFilter): Flow<List<Location>>
    suspend fun getLocationById(id: String): Location?
    suspend fun addLocation(location: Location)
    suspend fun updateLocation(location: Location)
    suspend fun deleteLocation(id: String)
    
    // History tracking methods
    fun getAllTracks(): Flow<List<LocationTrack>>
    fun getCurrentTrack(): Flow<LocationTrack?>
    suspend fun startNewTrack(name: String)
    suspend fun stopCurrentTrack()
    suspend fun saveLocationPoint(trackId: String, point: com.activemap.shared.service.GeoLocation)
    suspend fun getTrackPoints(trackId: String): List<com.activemap.shared.model.LocationPoint>
}
