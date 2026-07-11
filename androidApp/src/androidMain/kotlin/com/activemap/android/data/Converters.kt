package com.activemap.android.data

import androidx.room.TypeConverter
import com.activemap.shared.model.*

class Converters {
    @TypeConverter
    fun fromActivityType(value: ActivityType): String = value.name
    
    @TypeConverter
    fun toActivityType(value: String): ActivityType = ActivityType.valueOf(value)
    
    @TypeConverter
    fun fromCoverageLevel(value: CoverageLevel): String = value.name
    
    @TypeConverter
    fun toCoverageLevel(value: String): CoverageLevel = CoverageLevel.valueOf(value)
    
    @TypeConverter
    fun fromLightingLevel(value: LightingLevel): String = value.name
    
    @TypeConverter
    fun toLightingLevel(value: String): LightingLevel = LightingLevel.valueOf(value)
    
    @TypeConverter
    fun fromCleanlinessLevel(value: CleanlinessLevel): String = value.name
    
    @TypeConverter
    fun toCleanlinessLevel(value: String): CleanlinessLevel = CleanlinessLevel.valueOf(value)
    
    @TypeConverter
    fun fromNoiseLevel(value: NoiseLevel): String = value.name
    
    @TypeConverter
    fun toNoiseLevel(value: String): NoiseLevel = NoiseLevel.valueOf(value)
    
    @TypeConverter
    fun fromVisitStatus(value: VisitStatus): String = value.name
    
    @TypeConverter
    fun toVisitStatus(value: String): VisitStatus = VisitStatus.valueOf(value)
}