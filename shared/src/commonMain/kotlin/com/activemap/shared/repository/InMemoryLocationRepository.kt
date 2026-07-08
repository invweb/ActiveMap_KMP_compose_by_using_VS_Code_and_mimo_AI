package com.activemap.shared.repository

import com.activemap.shared.model.Location
import com.activemap.shared.model.LocationFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class InMemoryLocationRepository : LocationRepository {
    private val locations = MutableStateFlow<List<Location>>(emptyList())
    
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
    
    fun loadLocations(data: List<Location>) {
        locations.value = data
    }
}
