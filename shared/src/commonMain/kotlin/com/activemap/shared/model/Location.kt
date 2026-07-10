package com.activemap.shared.model

import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val id: String,
    val name: String,
    val activityType: ActivityType,
    val latitude: Double,
    val longitude: Double,
    val coverage: CoverageLevel = CoverageLevel.MEDIUM,
    val lighting: LightingLevel = LightingLevel.MEDIUM,
    val inventory: String = "",
    val cleanliness: CleanlinessLevel = CleanlinessLevel.MEDIUM,
    val noiseLevel: NoiseLevel = NoiseLevel.MEDIUM,
    val rating: Int = 3,
    val status: VisitStatus = VisitStatus.WANT_TO_VISIT,
    val notes: String = "",
    val photos: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Serializable
enum class ActivityType {
    SPORT,        // Sports
    WORK,         // Work
    REST,         // Leisure
    EDUCATION,    // Education
    ENTERTAINMENT // Entertainment
}

@Serializable
enum class CoverageLevel {
    NONE,      // No coverage
    PARTIAL,   // Partial
    MEDIUM,    // Medium
    FULL       // Full
}

@Serializable
enum class LightingLevel {
    NONE,      // No lighting
    LOW,       // Dim
    MEDIUM,    // Medium
    BRIGHT     // Bright
}

@Serializable
enum class CleanlinessLevel {
    DIRTY,     // Dirty
    POOR,      // Poor
    MEDIUM,    // Medium
    CLEAN,     // Clean
    PERFECT    // Perfect
}

@Serializable
enum class NoiseLevel {
    QUIET,     // Quiet
    LOW,       // Some noise
    MEDIUM,    // Medium noise
    LOUD,      // Loud
    VERY_LOUD  // Very loud
}

@Serializable
enum class VisitStatus {
    WAS_THERE,        // Visited
    WANT_TO_VISIT,    // Want to visit
    NOT_SUITABLE      // Not suitable
}

@Serializable
data class LocationFilter(
    val activityType: ActivityType? = null,
    val status: VisitStatus? = null,
    val searchQuery: String = ""
)
