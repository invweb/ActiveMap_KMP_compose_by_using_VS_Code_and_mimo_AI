package com.activemap.android.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.activemap.shared.model.*

@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey val id: String,
    val name: String,
    val activityType: ActivityType,
    val latitude: Double,
    val longitude: Double,
    val coverage: CoverageLevel,
    val lighting: LightingLevel,
    val inventory: String,
    val cleanliness: CleanlinessLevel,
    val noiseLevel: NoiseLevel,
    val rating: Int,
    val status: VisitStatus,
    val notes: String,
    val photos: String,
    val createdAt: Long,
    val updatedAt: Long
) {
    fun toLocation(): Location {
        return Location(
            id = id,
            name = name,
            activityType = activityType,
            latitude = latitude,
            longitude = longitude,
            coverage = coverage,
            lighting = lighting,
            inventory = inventory,
            cleanliness = cleanliness,
            noiseLevel = noiseLevel,
            rating = rating,
            status = status,
            notes = notes,
            photos = photos.split(",").filter { it.isNotBlank() },
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
    
    companion object {
        fun fromLocation(location: Location): LocationEntity {
            return LocationEntity(
                id = location.id,
                name = location.name,
                activityType = location.activityType,
                latitude = location.latitude,
                longitude = location.longitude,
                coverage = location.coverage,
                lighting = location.lighting,
                inventory = location.inventory,
                cleanliness = location.cleanliness,
                noiseLevel = location.noiseLevel,
                rating = location.rating,
                status = location.status,
                notes = location.notes,
                photos = location.photos.joinToString(","),
                createdAt = location.createdAt,
                updatedAt = location.updatedAt
            )
        }
    }
}