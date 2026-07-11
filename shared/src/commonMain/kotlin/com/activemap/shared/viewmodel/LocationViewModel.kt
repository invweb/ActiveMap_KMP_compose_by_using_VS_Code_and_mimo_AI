package com.activemap.shared.viewmodel

import com.activemap.shared.model.*
import com.activemap.shared.repository.LocationRepository
import com.activemap.shared.service.DataExporter
import com.activemap.shared.service.GeoLocation
import com.activemap.shared.service.LocationService
import com.activemap.shared.service.OfflineRouteService
import com.activemap.shared.service.OsrmService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class LocationViewModel(
    private val repository: LocationRepository,
    private val locationService: LocationService,
    coroutineContext: CoroutineContext = Dispatchers.Default
) {
    private val supervisorJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(supervisorJob + coroutineContext)
    private val osrmService = OsrmService()
    private val offlineRouteService = OfflineRouteService()
    private val dataExporter = DataExporter(repository)
    
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
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _operationSuccess = MutableStateFlow<String?>(null)
    val operationSuccess: StateFlow<String?> = _operationSuccess.asStateFlow()
    
    private val _currentLocation = MutableStateFlow<GeoLocation?>(null)
    val currentLocation: StateFlow<GeoLocation?> = _currentLocation.asStateFlow()
    
    private val _isLocationLoading = MutableStateFlow(false)
    val isLocationLoading: StateFlow<Boolean> = _isLocationLoading.asStateFlow()
    
    init {
        viewModelScope.launch {
            repository.getAllLocations()
                .catch { e ->
                    _error.value = "Ошибка загрузки локаций: ${e.message}"
                }
                .collect { locationList ->
                    _locations.value = locationList
                }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
    
    fun clearSuccess() {
        _operationSuccess.value = null
    }
    
    fun selectLocation(location: Location?) {
        _selectedLocation.value = location
    }
    
    fun updateFilter(filter: LocationFilter) {
        _currentFilter.value = filter
        viewModelScope.launch {
            repository.getFilteredLocations(filter)
                .catch { e ->
                    _error.value = "Ошибка фильтрации: ${e.message}"
                }
                .collect { filteredList ->
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
        if (location.name.isBlank()) {
            _error.value = "Название обязательно"
            return
        }
        if (location.latitude < -90.0 || location.latitude > 90.0) {
            _error.value = "Широта должна быть от -90 до 90"
            return
        }
        if (location.longitude < -180.0 || location.longitude > 180.0) {
            _error.value = "Долгота должна быть от -180 до 180"
            return
        }
        if (location.rating < 1 || location.rating > 5) {
            _error.value = "Рейтинг должен быть от 1 до 5"
            return
        }
        
        viewModelScope.launch {
            try {
                repository.addLocation(location)
                _isAddingLocation.value = false
                _operationSuccess.value = "Локация добавлена"
                onComplete()
            } catch (e: Exception) {
                _error.value = "Ошибка сохранения: ${e.message}"
            }
        }
    }
    
    fun updateLocation(location: Location) {
        viewModelScope.launch {
            try {
                repository.updateLocation(location)
                _selectedLocation.value = location
                _operationSuccess.value = "Локация обновлена"
            } catch (e: Exception) {
                _error.value = "Ошибка обновления: ${e.message}"
            }
        }
    }
    
    fun deleteLocation(id: String) {
        viewModelScope.launch {
            try {
                repository.deleteLocation(id)
                _selectedLocation.value = null
                _operationSuccess.value = "Локация удалена"
            } catch (e: Exception) {
                _error.value = "Ошибка удаления: ${e.message}"
            }
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
    
    fun centerOnMe() {
        viewModelScope.launch {
            _isLocationLoading.value = true
            try {
                val location = locationService.getCurrentLocation()
                if (location != null) {
                    _currentLocation.value = location
                } else {
                    _error.value = "Не удалось определить местоположение"
                }
            } catch (e: Exception) {
                _error.value = "Ошибка геолокации: ${e.message}"
            } finally {
                _isLocationLoading.value = false
            }
        }
    }
    
    private fun calculateRoute() {
        val start = _routeStart.value ?: return
        val end = _routeEnd.value ?: return
        
        viewModelScope.launch {
            _isCalculatingRoute.value = true
            _routeError.value = null
            try {
                val route = try {
                    osrmService.getRoute(
                        startLat = start.first,
                        startLng = start.second,
                        endLat = end.first,
                        endLng = end.second
                    )
                } catch (e: Exception) {
                    offlineRouteService.calculateStraightLineRoute(
                        startLat = start.first,
                        startLng = start.second,
                        endLat = end.first,
                        endLng = end.second
                    )
                }
                _currentRoute.value = route
            } catch (e: Exception) {
                _routeError.value = "Ошибка построения маршрута: ${e.message}"
            } finally {
                _isCalculatingRoute.value = false
            }
        }
    }
    
    fun close() {
        supervisorJob.cancel()
        osrmService.close()
    }
    
    suspend fun exportData(): String {
        return try {
            dataExporter.exportToJson()
        } catch (e: Exception) {
            _error.value = "Ошибка экспорта: ${e.message}"
            ""
        }
    }
    
    suspend fun importData(jsonString: String): Int {
        return try {
            val count = dataExporter.importFromJson(jsonString)
            _operationSuccess.value = "Импортировано локаций: $count"
            count
        } catch (e: Exception) {
            _error.value = "Ошибка импорта: ${e.message}"
            0
        }
    }
}