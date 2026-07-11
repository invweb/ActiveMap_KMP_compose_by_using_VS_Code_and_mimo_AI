package com.activemap.android.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Query("SELECT * FROM locations")
    fun getAllLocations(): Flow<List<LocationEntity>>
    
    @Query("SELECT * FROM locations WHERE id = :id")
    suspend fun getLocationById(id: String): LocationEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LocationEntity)
    
    @Update
    suspend fun updateLocation(location: LocationEntity)
    
    @Query("DELETE FROM locations WHERE id = :id")
    suspend fun deleteLocation(id: String)
}