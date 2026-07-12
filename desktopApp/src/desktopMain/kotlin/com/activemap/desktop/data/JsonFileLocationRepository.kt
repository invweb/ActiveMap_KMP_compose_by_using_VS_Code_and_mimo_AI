package com.activemap.desktop.data

import com.activemap.shared.model.Location
import com.activemap.shared.model.LocationFilter
import com.activemap.shared.model.LocationPoint
import com.activemap.shared.model.LocationTrack
import com.activemap.shared.repository.LocationRepository
import com.activemap.shared.service.GeoLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.io.File

class JsonFileLocationRepository : LocationRepository {
    private val locations = MutableStateFlow<List<Location>>(emptyList())
    private val tracks = MutableStateFlow<List<LocationTrack>>(emptyList())
    private val trackPoints = mutableMapOf<String, MutableList<LocationPoint>>()
    private val json = Json { ignoreUnknownKeys = true }
    private val dataFile: File
    private val tracksFile: File
    
    init {
        val appDir = File(System.getProperty("user.home"), ".activemap")
        if (!appDir.exists()) {
            appDir.mkdirs()
        }
        dataFile = File(appDir, "locations.json")
        tracksFile = File(appDir, "tracks.json")
        loadFromFile()
        loadTracksFromFile()
    }
    
    private fun loadFromFile() {
        if (dataFile.exists()) {
            try {
                val data = dataFile.readText()
                if (data.isNotBlank()) {
                    val locationList = json.decodeFromString<List<Location>>(data)
                    locations.value = locationList
                }
            } catch (e: Exception) {
                e.printStackTrace()
                locations.value = emptyList()
            }
        }
    }
    
    private fun saveToFile() {
        try {
            val data = json.encodeToString(locations.value)
            dataFile.writeText(data)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun loadTracksFromFile() {
        if (tracksFile.exists()) {
            try {
                val data = tracksFile.readText()
                if (data.isNotBlank()) {
                    val trackList = json.decodeFromString<List<LocationTrack>>(data)
                    tracks.value = trackList
                    trackPoints.clear()
                    trackList.forEach { track ->
                        trackPoints[track.id] = track.points.toMutableList()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                tracks.value = emptyList()
            }
        }
    }
    
    private fun saveTracksToFile() {
        try {
            val data = json.encodeToString(tracks.value)
            tracksFile.writeText(data)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
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
        saveToFile()
    }
    
    override suspend fun updateLocation(location: Location) {
        locations.value = locations.value.map { 
            if (it.id == location.id) location else it 
        }
        saveToFile()
    }
    
    override suspend fun deleteLocation(id: String) {
        locations.value = locations.value.filter { it.id != id }
        saveToFile()
    }
    
    override fun getAllTracks(): Flow<List<LocationTrack>> = tracks
    
    override fun getCurrentTrack(): Flow<LocationTrack?> {
        return tracks.map { list -> list.find { it.endDate == null } }
    }
    
    override suspend fun startNewTrack(name: String) {
        val track = LocationTrack.startNew(name)
        tracks.value = tracks.value + track
        trackPoints[track.id] = mutableListOf()
        saveTracksToFile()
    }
    
    override suspend fun stopCurrentTrack() {
        val currentTrack = tracks.value.find { it.endDate == null }
        currentTrack?.let {
            val stopped = it.stop()
            tracks.value = tracks.value.map { t -> if (t.id == it.id) stopped else t }
            saveTracksToFile()
        }
    }
    
    override suspend fun saveLocationPoint(trackId: String, point: GeoLocation) {
        val locationPoint = LocationPoint.fromGeoLocation(point)
        val pointsList = trackPoints.getOrPut(trackId) { mutableListOf() }
        pointsList.add(locationPoint)
        
        tracks.value = tracks.value.map { track ->
            if (track.id == trackId) {
                val newPoints = track.points + locationPoint
                val newDistance = calculateDistance(newPoints)
                track.addPoint(locationPoint).copy(distanceMeters = newDistance)
            } else {
                track
            }
        }
        saveTracksToFile()
    }
    
    override suspend fun getTrackPoints(trackId: String): List<LocationPoint> {
        return trackPoints[trackId]?.toList() ?: emptyList()
    }
    
    private fun calculateDistance(points: List<LocationPoint>): Double {
        if (points.size < 2) return 0.0
        var totalDistance = 0.0
        for (i in 1 until points.size) {
            totalDistance += haversineDistance(
                points[i - 1].latitude, points[i - 1].longitude,
                points[i].latitude, points[i].longitude
            )
        }
        return totalDistance
    }
    
    private fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
                kotlin.math.cos(Math.toRadians(lat1)) * kotlin.math.cos(Math.toRadians(lat2)) *
                kotlin.math.sin(dLon / 2) * kotlin.math.sin(dLon / 2)
        val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
        return R * c
    }
}