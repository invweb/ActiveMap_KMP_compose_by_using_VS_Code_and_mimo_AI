package com.activemap.web.data

import com.activemap.shared.model.Location
import com.activemap.shared.model.LocationFilter
import com.activemap.shared.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.browser.window

class LocalStorageLocationRepository : LocationRepository {
    private val locations = MutableStateFlow<List<Location>>(emptyList())
    private val json = Json { ignoreUnknownKeys = true }
    private val storageKey = "activemap_locations"
    
    init {
        loadFromStorage()
    }
    
    private fun loadFromStorage() {
        try {
            val data = window.localStorage.getItem(storageKey)
            if (data != null && data.isNotBlank()) {
                val locationList = json.decodeFromString<List<Location>>(data)
                locations.value = locationList
            }
        } catch (e: Exception) {
            console.error("Failed to load locations from localStorage", e)
            locations.value = emptyList()
        }
    }
    
    private fun saveToStorage() {
        try {
            val data = json.encodeToString(locations.value)
            window.localStorage.setItem(storageKey, data)
        } catch (e: Exception) {
            console.error("Failed to save locations to localStorage", e)
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
        saveToStorage()
    }
    
    override suspend fun updateLocation(location: Location) {
        locations.value = locations.value.map { 
            if (it.id == location.id) location else it 
        }
        saveToStorage()
    }
    
    override suspend fun deleteLocation(id: String) {
        locations.value = locations.value.filter { it.id != id }
        saveToStorage()
    }
}