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
    SPORT,        // Спорт
    WORK,         // Работа
    REST,         // Отдых
    EDUCATION,    // Образование
    ENTERTAINMENT // Развлечения
}

@Serializable
enum class CoverageLevel {
    NONE,      // Без покрытия
    PARTIAL,   // Частичное
    MEDIUM,    // Среднее
    FULL       // Полное
}

@Serializable
enum class LightingLevel {
    NONE,      // Без освещения
    LOW,       // Слабое
    MEDIUM,    // Среднее
    BRIGHT     // Яркое
}

@Serializable
enum class CleanlinessLevel {
    DIRTY,     // Грязно
    POOR,      // Плохо
    MEDIUM,    // Средне
    CLEAN,     // Чисто
    PERFECT    // Идеально
}

@Serializable
enum class NoiseLevel {
    QUIET,     // Тихо
    LOW,       // Немного шума
    MEDIUM,    // Средний шум
    LOUD,      // Шумно
    VERY_LOUD  // Очень шумно
}

@Serializable
enum class VisitStatus {
    WAS_THERE,        // Был
    WANT_TO_VISIT,    // Хочу сходить
    NOT_SUITABLE      // Не подходит
}

@Serializable
data class LocationFilter(
    val activityType: ActivityType? = null,
    val status: VisitStatus? = null,
    val searchQuery: String = ""
)
