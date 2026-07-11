package com.activemap.web.data

import com.activemap.web.model.Location
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.browser.window

class LocalStorageLocationRepository {
    private var locations = mutableListOf<Location>()
    private val json = Json { ignoreUnknownKeys = true }
    private val storageKey = "activemap_locations"

    init { loadFromStorage() }

    private fun loadFromStorage() {
        try {
            val data = window.localStorage.getItem(storageKey)
            if (data != null && data.isNotBlank()) {
                locations = json.decodeFromString<List<Location>>(data).toMutableList()
            }
        } catch (e: Exception) {
            console.error("Failed to load from localStorage", e)
        }
    }

    private fun saveToStorage() {
        try {
            window.localStorage.setItem(storageKey, json.encodeToString(locations))
        } catch (e: Exception) {
            console.error("Failed to save to localStorage", e)
        }
    }

    fun getLocations(): List<Location> = locations.toList()

    fun addLocation(location: Location) {
        locations.add(location)
        saveToStorage()
    }

    fun deleteLocation(id: String) {
        locations.removeAll { it.id == id }
        saveToStorage()
    }
}
