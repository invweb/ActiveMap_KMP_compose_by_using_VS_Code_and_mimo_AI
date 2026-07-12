package com.activemap.android.data

import com.activemap.shared.model.Location
import com.activemap.shared.model.LocationFilter
import com.activemap.shared.model.LocationPoint
import com.activemap.shared.model.LocationTrack
import com.activemap.shared.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomLocationRepository(
    private val dao: LocationDao,
    private val trackDao: TrackDao,
    private val trackPointDao: TrackPointDao
) : LocationRepository {
    override fun getAllLocations(): Flow<List<Location>> {
        return dao.getAllLocations().map { entities ->
            entities.map { it.toLocation() }
        }
    }
    
    override fun getFilteredLocations(filter: LocationFilter): Flow<List<Location>> {
        return dao.getAllLocations().map { entities ->
            entities.map { it.toLocation() }.filter { location ->
                val matchesType = filter.activityType == null || location.activityType == filter.activityType
                val matchesStatus = filter.status == null || location.status == filter.status
                val matchesSearch = filter.searchQuery.isEmpty() || 
                    location.name.contains(filter.searchQuery, ignoreCase = true)
                matchesType && matchesStatus && matchesSearch
            }
        }
    }
    
    override suspend fun getLocationById(id: String): Location? {
        return dao.getLocationById(id)?.toLocation()
    }
    
    override suspend fun addLocation(location: Location) {
        dao.insertLocation(LocationEntity.fromLocation(location))
    }
    
    override suspend fun updateLocation(location: Location) {
        dao.updateLocation(LocationEntity.fromLocation(location))
    }
    
    override suspend fun deleteLocation(id: String) {
        dao.deleteLocation(id)
    }
    
    override fun getAllTracks(): Flow<List<LocationTrack>> {
        return trackDao.getAllTracks().map { entities ->
            entities.map { it.toLocationTrack() }
        }
    }
    
    override fun getCurrentTrack(): Flow<LocationTrack?> {
        return trackDao.getAllTracks().map { entities ->
            entities.find { it.endDate == null }
        }
    }
    
    override suspend fun startNewTrack(name: String) {
        val track = LocationTrack.startNew(name)
        trackDao.insertTrack(TrackEntity.fromLocationTrack(track))
    }
    
    override suspend fun stopCurrentTrack() {
        val currentTrack = trackDao.getAllTracks().firstOrNull { it.endDate == null }
        currentTrack?.let { existing ->
            val stopped = existing.toLocationTrack().stop()
            trackDao.updateTrack(TrackEntity.fromLocationTrack(stopped))
        }
    }
    
    override suspend fun saveLocationPoint(trackId: String, point: com.activemap.shared.service.GeoLocation) {
        val trackPoint = TrackPointEntity(
            trackId = trackId,
            latitude = point.latitude,
            longitude = point.longitude,
            timestamp = System.currentTimeMillis(),
            accuracy = point.accuracy,
            speed = null
        )
        trackPointDao.insertTrackPoints(listOf(trackPoint))
        
        // Update track distance
        val points = trackPointDao.getTrackPointsList(trackId)
        val track = trackDao.getTrackById(trackId)?.toLocationTrack()
        track?.let { existing ->
            val newDistance = calculateDistance(points.map { it.toLocationPoint() })
            trackDao.updateTrack(TrackEntity.fromLocationTrack(existing.copy(distanceMeters = newDistance)))
        }
    }
    
    override suspend fun getTrackPoints(trackId: String): List<LocationPoint> {
        return trackPointDao.getTrackPointsList(trackId).map { it.toLocationPoint() }
    }
    
    private fun calculateDistance(points: List<LocationPoint>): Double {
        if (points.size < 2) return 0.0
        
        var totalDistance = 0.0
        for (i in 1 until points.size) {
            totalDistance += haversineDistance(
                points[i - 1].latitude,
                points[i - 1].longitude,
                points[i].latitude,
                points[i].longitude
            )
        }
        return totalDistance
    }
    
    private fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371000.0 // Earth radius in meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }
    
    private fun sin(x: Double): Double = kotlin.math.sin(x)
    private fun cos(x: Double): Double = kotlin.math.cos(x)
    private fun sqrt(x: Double): Double = kotlin.math.sqrt(x)
    private fun atan2(y: Double, x: Double): Double = kotlin.math.atan2(y, x)
}