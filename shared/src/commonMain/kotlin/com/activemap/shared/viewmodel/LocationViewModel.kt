package com.activemap.shared.viewmodel

import com.activemap.shared.model.*
import com.activemap.shared.repository.LocationRepository
import com.activemap.shared.service.OsrmService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LocationViewModel(
    private val repository: LocationRepository
) {
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val osrmService = OsrmService()
    
    private val _locations = MutableStateFlow<List<Location>>(emptyList())
    val locations: StateFlow<List<Location>> = _locations.asStateFlow()
    
    private val _selectedLocation = MutableStateFlow<Location?>(null)
    val selectedLocation: StateFlow<Location?> = _selectedLocation.asStateFlow()
    
    private val _currentFilter = MutableStateFlow(LocationFilter())
    val currentFilter: StateFlow<LocationFilter> = _currentFilter.asStateFlow()
    
    private val _isAddingLocation = MutableStateFlow(false)
    val isAddingLocation: StateFlow<Boolean> = _isAddingLocation.asStateFlow()
    
    private val _pickedLatLng = MutableStateFlow<Pair<Double, Double>?>(null)
    val pickedLatLng: StateFlow<Pair<Double, Double>?> = _pickedLatLng.asStateFlow()
    
    private val _isRouteMode = MutableStateFlow(false)
    val isRouteMode: StateFlow<Boolean> = _isRouteMode.asStateFlow()
    
    private val _routeStart = MutableStateFlow<Pair<Double, Double>?>(null)
    val routeStart: StateFlow<Pair<Double, Double>?> = _routeStart.asStateFlow()
    
    private val _routeEnd = MutableStateFlow<Pair<Double, Double>?>(null)
    val routeEnd: StateFlow<Pair<Double, Double>?> = _routeEnd.asStateFlow()
    
    private val _currentRoute = MutableStateFlow<Route?>(null)
    val currentRoute: StateFlow<Route?> = _currentRoute.asStateFlow()
    
    private val _isCalculatingRoute = MutableStateFlow(false)
    val isCalculatingRoute: StateFlow<Boolean> = _isCalculatingRoute.asStateFlow()
    
    private val _routeError = MutableStateFlow<String?>(null)
    val routeError: StateFlow<String?> = _routeError.asStateFlow()
    
    init {
        viewModelScope.launch {
            repository.getAllLocations().collect { locationList ->
                _locations.value = locationList
            }
        }
    }
    
    fun selectLocation(location: Location?) {
        _selectedLocation.value = location
    }
    
    fun updateFilter(filter: LocationFilter) {
        _currentFilter.value = filter
        viewModelScope.launch {
            repository.getFilteredLocations(filter).collect { filteredList ->
                _locations.value = filteredList
            }
        }
    }
    
    fun startAddingLocation() {
        _pickedLatLng.value = null
        _isAddingLocation.value = true
    }
    
    fun startAddingLocationAt(lat: Double, lng: Double) {
        _pickedLatLng.value = lat to lng
        _isAddingLocation.value = true
    }
    
    fun cancelAddingLocation() {
        _isAddingLocation.value = false
        _pickedLatLng.value = null
    }
    
    fun saveLocation(location: Location, onComplete: () -> Unit) {
        viewModelScope.launch {
            if (location.name.isBlank()) {
                throw IllegalArgumentException("Название обязательно")
            }
            repository.addLocation(location)
            _isAddingLocation.value = false
            onComplete()
        }
    }
    
    fun updateLocation(location: Location) {
        viewModelScope.launch {
            repository.updateLocation(location)
            _selectedLocation.value = location
        }
    }
    
    fun deleteLocation(id: String) {
        viewModelScope.launch {
            repository.deleteLocation(id)
            _selectedLocation.value = null
        }
    }
    
    fun toggleRouteMode() {
        _isRouteMode.value = !_isRouteMode.value
        if (!_isRouteMode.value) {
            clearRoute()
        }
    }
    
    fun setRoutePoint(lat: Double, lng: Double) {
        if (_routeStart.value == null) {
            _routeStart.value = lat to lng
            _routeEnd.value = null
            _currentRoute.value = null
        } else if (_routeEnd.value == null) {
            _routeEnd.value = lat to lng
            calculateRoute()
        }
    }
    
    fun clearRoute() {
        _routeStart.value = null
        _routeEnd.value = null
        _currentRoute.value = null
        _routeError.value = null
    }
    
    private fun calculateRoute() {
        val start = _routeStart.value ?: return
        val end = _routeEnd.value ?: return
        
        viewModelScope.launch {
            _isCalculatingRoute.value = true
            _routeError.value = null
            try {
                val route = osrmService.getRoute(
                    startLat = start.first,
                    startLng = start.second,
                    endLat = end.first,
                    endLng = end.second
                )
                _currentRoute.value = route
            } catch (e: Exception) {
                _routeError.value = e.message ?: "Ошибка построения маршрута"
            } finally {
                _isCalculatingRoute.value = false
            }
        }
    }
}
