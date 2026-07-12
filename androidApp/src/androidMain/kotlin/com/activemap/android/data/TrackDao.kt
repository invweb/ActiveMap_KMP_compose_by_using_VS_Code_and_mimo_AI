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

@Dao
interface TrackDao {
    @Query("SELECT * FROM tracks ORDER BY startDate DESC")
    fun getAllTracks(): Flow<List<TrackEntity>>
    
    @Query("SELECT * FROM tracks WHERE id = :id")
    suspend fun getTrackById(id: String): TrackEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)
    
    @Update
    suspend fun updateTrack(track: TrackEntity)
    
    @Query("DELETE FROM tracks WHERE id = :id")
    suspend fun deleteTrack(id: String)
}

@Dao
interface TrackPointDao {
    @Query("SELECT * FROM track_points WHERE trackId = :trackId ORDER BY timestamp ASC")
    fun getTrackPoints(trackId: String): Flow<List<TrackPointEntity>>
    
    @Query("SELECT * FROM track_points WHERE trackId = :trackId ORDER BY timestamp ASC")
    suspend fun getTrackPointsList(trackId: String): List<TrackPointEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackPoints(points: List<TrackPointEntity>)
    
    @Query("DELETE FROM track_points WHERE trackId = :trackId")
    suspend fun deleteTrackPoints(trackId: String)
}
