package com.activemap.desktop.data

import com.activemap.shared.model.Location
import com.activemap.shared.model.LocationFilter
import com.activemap.shared.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.io.File

class JsonFileLocationRepository : LocationRepository {
    private val locations = MutableStateFlow<List<Location>>(emptyList())
    private val json = Json { ignoreUnknownKeys = true }
    private val dataFile: File
    
    init {
        val appDir = File(System.getProperty("user.home"), ".activemap")
        if (!appDir.exists()) {
            appDir.mkdirs()
        }
        dataFile = File(appDir, "locations.json")
        loadFromFile()
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
}