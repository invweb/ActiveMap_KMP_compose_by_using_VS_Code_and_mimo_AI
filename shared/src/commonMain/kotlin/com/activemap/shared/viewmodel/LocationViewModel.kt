package com.activemap.shared.viewmodel

import com.activemap.shared.model.*
import com.activemap.shared.repository.LocationRepository
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
}
