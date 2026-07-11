package com.activemap.shared.service

import com.activemap.shared.model.Location
import com.activemap.shared.repository.LocationRepository
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DataExporter(
    private val repository: LocationRepository
) {
    private val json = Json { 
        prettyPrint = true
        ignoreUnknownKeys = true 
    }
    
    suspend fun exportToJson(): String {
        val locations = repository.getAllLocations().first()
        return json.encodeToString(locations)
    }
    
    suspend fun importFromJson(jsonString: String): Int {
        return try {
            val locations = json.decodeFromString<List<Location>>(jsonString)
            var importedCount = 0
            for (location in locations) {
                repository.addLocation(location)
                importedCount++
            }
            importedCount
        } catch (e: Exception) {
            throw IllegalArgumentException("Ошибка импорта: ${e.message}")
        }
    }
    
    fun getExportFileName(): String {
        val timestamp = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        return "activemap_export_$timestamp.json"
    }
}