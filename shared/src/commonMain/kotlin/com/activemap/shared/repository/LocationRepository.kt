package com.activemap.shared.repository

import com.activemap.shared.model.Location
import com.activemap.shared.model.LocationFilter
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun getAllLocations(): Flow<List<Location>>
    fun getFilteredLocations(filter: LocationFilter): Flow<List<Location>>
    suspend fun getLocationById(id: String): Location?
    suspend fun addLocation(location: Location)
    suspend fun updateLocation(location: Location)
    suspend fun deleteLocation(id: String)
}
