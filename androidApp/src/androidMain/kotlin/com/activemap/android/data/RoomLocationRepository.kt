package com.activemap.android.data

import com.activemap.shared.model.Location
import com.activemap.shared.model.LocationFilter
import com.activemap.shared.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomLocationRepository(private val dao: LocationDao) : LocationRepository {
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
}