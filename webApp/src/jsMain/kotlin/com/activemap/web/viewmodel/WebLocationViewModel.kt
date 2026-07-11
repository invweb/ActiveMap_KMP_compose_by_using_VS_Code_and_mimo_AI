package com.activemap.web.viewmodel

import com.activemap.web.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WebLocationViewModel(
    private val repository: WebLocationRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _locations = MutableStateFlow<List<Location>>(emptyList())
    val locations: StateFlow<List<Location>> = _locations.asStateFlow()

    private val _selectedLocation = MutableStateFlow<Location?>(null)
    val selectedLocation: StateFlow<Location?> = _selectedLocation.asStateFlow()

    private val _currentFilter = MutableStateFlow(LocationFilter())
    val currentFilter: StateFlow<LocationFilter> = _currentFilter.asStateFlow()

    private val _isAddingLocation = MutableStateFlow(false)
    val isAddingLocation: StateFlow<Boolean> = _isAddingLocation.asStateFlow()

    private val _pickedPoint = MutableStateFlow<Pair<Double, Double>?>(null)
    val pickedPoint: StateFlow<Pair<Double, Double>?> = _pickedPoint.asStateFlow()

    private val _isRouteMode = MutableStateFlow(false)
    val isRouteMode: StateFlow<Boolean> = _isRouteMode.asStateFlow()

    private val _routeWaypoints = MutableStateFlow<List<Pair<Double, Double>>>(emptyList())
    val routeWaypoints: StateFlow<List<Pair<Double, Double>>> = _routeWaypoints.asStateFlow()

    private val _selectedRouteLocations = MutableStateFlow<List<Location>>(emptyList())
    val selectedRouteLocations: StateFlow<List<Location>> = _selectedRouteLocations.asStateFlow()

    private val _currentRoute = MutableStateFlow<Route?>(null)
    val currentRoute: StateFlow<Route?> = _currentRoute.asStateFlow()

    init {
        scope.launch {
            repository.getAllLocations().collect { _locations.value = it }
        }
    }

    fun selectLocation(location: Location?) { _selectedLocation.value = location }

    fun updateFilter(filter: LocationFilter) {
        _currentFilter.value = filter
        scope.launch {
            repository.getFilteredLocations(filter).collect { _locations.value = it }
        }
    }

    fun startAddingLocationAt(lat: Double, lng: Double) {
        _pickedPoint.value = lat to lng
        _isAddingLocation.value = true
    }

    fun cancelAddingLocation() {
        _isAddingLocation.value = false
        _pickedPoint.value = null
    }

    fun saveLocation(location: Location, onComplete: () -> Unit) {
        scope.launch {
            repository.addLocation(location)
            _isAddingLocation.value = false
            _pickedPoint.value = null
            onComplete()
        }
    }

    fun updateLocation(location: Location) {
        scope.launch {
            repository.updateLocation(location)
            _selectedLocation.value = null
        }
    }

    fun deleteLocation(id: String) {
        scope.launch {
            repository.deleteLocation(id)
            _selectedLocation.value = null
        }
    }

    fun toggleRouteMode() {
        _isRouteMode.value = !_isRouteMode.value
        if (!_isRouteMode.value) {
            _routeWaypoints.value = emptyList()
            _selectedRouteLocations.value = emptyList()
            _currentRoute.value = null
        }
    }

    fun setRoutePoint(lat: Double, lng: Double) {
        _routeWaypoints.value = _routeWaypoints.value + (lat to lng)
    }

    fun toggleRouteLocation(location: Location) {
        val current = _selectedRouteLocations.value
        _selectedRouteLocations.value = if (location in current) current - location else current + location
    }

    fun clearRoute() {
        _routeWaypoints.value = emptyList()
        _selectedRouteLocations.value = emptyList()
        _currentRoute.value = null
    }

    fun clearSelectedLocations() {
        _selectedRouteLocations.value = emptyList()
    }

    fun close() {}
}

interface WebLocationRepository {
    fun getAllLocations(): kotlinx.coroutines.flow.Flow<List<Location>>
    fun getFilteredLocations(filter: LocationFilter): kotlinx.coroutines.flow.Flow<List<Location>>
    suspend fun addLocation(location: Location)
    suspend fun updateLocation(location: Location)
    suspend fun deleteLocation(id: String)
}
