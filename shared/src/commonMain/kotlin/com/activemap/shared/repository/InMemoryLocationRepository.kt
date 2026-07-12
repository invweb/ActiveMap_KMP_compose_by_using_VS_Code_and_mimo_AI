package com.activemap.shared.repository

import com.activemap.shared.model.Location
import com.activemap.shared.model.LocationFilter
import com.activemap.shared.model.LocationPoint
import com.activemap.shared.model.LocationTrack
import com.activemap.shared.service.GeoLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class InMemoryLocationRepository : LocationRepository {
    private val locations = MutableStateFlow<List<Location>>(emptyList())
    private val tracks = MutableStateFlow<List<LocationTrack>>(emptyList())
    private val trackPoints = mutableMapOf<String, MutableList<LocationPoint>>()
    
    override fun getAllLocations(): Flow<List<Location>> = locations
    
    override fun getFilteredLocations(filter: LocationFilter): Flow<List<Location>> {
        return locations.map { list ->
            list.filter { location ->
                val matchesType = filter.activityType == null || location.activityType == filter.activityType
                val matchesStatus = filter.status == null || location.status == filter.status
                val matchesSearch = filter.searchQuery.isEmpty() || 
                    location.name.contains(filter.searchQuery, ignoreCase = true)
                matchesType && matchesStatus && matchesSearch
            }
        }
    }
    
    override suspend fun getLocationById(id: String): Location? {
        return locations.value.find { it.id == id }
    }
    
    override suspend fun addLocation(location: Location) {
        locations.value = locations.value + location
    }
    
    override suspend fun updateLocation(location: Location) {
        locations.value = locations.value.map { 
            if (it.id == location.id) location else it 
        }
    }
    
    override suspend fun deleteLocation(id: String) {
        locations.value = locations.value.filter { it.id != id }
    }
    
    override fun getAllTracks(): Flow<List<LocationTrack>> = tracks
    
    override fun getCurrentTrack(): Flow<LocationTrack?> {
        return tracks.map { list -> list.find { it.endDate == null } }
    }
    
    override suspend fun startNewTrack(name: String) {
        val track = LocationTrack.startNew(name)
        tracks.value = tracks.value + track
        trackPoints[track.id] = mutableListOf()
    }
    
    override suspend fun stopCurrentTrack() {
        val currentTrack = tracks.value.find { it.endDate == null }
        currentTrack?.let {
            val stopped = it.stop()
            tracks.value = tracks.value.map { t -> if (t.id == it.id) stopped else t }
        }
    }
    
    override suspend fun saveLocationPoint(trackId: String, point: GeoLocation) {
        val locationPoint = LocationPoint.fromGeoLocation(point)
        val pointsList = trackPoints.getOrPut(trackId) { mutableListOf() }
        pointsList.add(locationPoint)
        
        // Update track
        tracks.value = tracks.value.map { track ->
            if (track.id == trackId) {
                val newPoints = track.points + locationPoint
                val newDistance = calculateDistance(newPoints)
                track.addPoint(locationPoint).copy(distanceMeters = newDistance)
            } else {
                track
            }
        }
    }
    
    override suspend fun getTrackPoints(trackId: String): List<LocationPoint> {
        return trackPoints[trackId]?.toList() ?: emptyList()
    }
    
    fun loadLocations(data: List<Location>) {
        locations.value = data
    }
    
    fun loadTracks(data: List<LocationTrack>) {
        tracks.value = data
        trackPoints.clear()
        data.forEach { track ->
            trackPoints[track.id] = track.points.toMutableList()
        }
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
        val R = 6371000.0
        val dLat = kotlin.math.toRadians(lat2 - lat1)
        val dLon = kotlin.math.toRadians(lon2 - lon1)
        val a = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
                kotlin.math.cos(kotlin.math.toRadians(lat1)) * kotlin.math.cos(kotlin.math.toRadians(lat2)) *
                kotlin.math.sin(dLon / 2) * kotlin.math.sin(dLon / 2)
        val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
        return R * c
    }
}
