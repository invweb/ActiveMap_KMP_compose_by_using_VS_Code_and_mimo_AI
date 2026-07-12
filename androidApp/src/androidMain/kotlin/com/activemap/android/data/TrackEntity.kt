package com.activemap.android.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.activemap.shared.model.LocationPoint
import com.activemap.shared.model.LocationTrack

@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey val id: String,
    val name: String,
    val startDate: Long,
    val endDate: Long?,
    val distanceMeters: Double,
    val durationMs: Long?
) {
    fun toLocationTrack(): LocationTrack {
        return LocationTrack(
            id = id,
            name = name,
            startDate = startDate,
            endDate = endDate,
            distanceMeters = distanceMeters,
            durationMs = durationMs
        )
    }
    
    companion object {
        fun fromLocationTrack(track: LocationTrack): TrackEntity {
            return TrackEntity(
                id = track.id,
                name = track.name,
                startDate = track.startDate,
                endDate = track.endDate,
                distanceMeters = track.distanceMeters,
                durationMs = track.durationMs
            )
        }
    }
}

@Entity(tableName = "track_points", primaryKeys = ["trackId", "timestamp"])
data class TrackPointEntity(
    val trackId: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val accuracy: Float?,
    val speed: Float?
) {
    fun toLocationPoint(): LocationPoint {
        return LocationPoint(
            latitude = latitude,
            longitude = longitude,
            timestamp = timestamp,
            accuracy = accuracy,
            speed = speed
        )
    }
    
    companion object {
        fun fromLocationPoint(trackId: String, point: LocationPoint): TrackPointEntity {
            return TrackPointEntity(
                trackId = trackId,
                latitude = point.latitude,
                longitude = point.longitude,
                timestamp = point.timestamp,
                accuracy = point.accuracy,
                speed = point.speed
            )
        }
    }
}
