package com.activemap.desktop.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.activemap.shared.viewmodel.LocationViewModel
import com.activemap.desktop.ui.components.MapViewDesktop
import com.activemap.shared.ui.Screen
import com.activemap.shared.ui.SharedLocationList
import com.activemap.shared.ui.SharedHistoryScreen
import androidx.compose.material.icons.filled.DateRange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveMapDesktopApp(viewModel: LocationViewModel) {
    val locations by viewModel.locations.collectAsState()
    val selectedLocation by viewModel.selectedLocation.collectAsState()
    val isAddingLocation by viewModel.isAddingLocation.collectAsState()
    val currentFilter by viewModel.currentFilter.collectAsState()
    val pickedPoint by viewModel.pickedPoint.collectAsState()
    val isRouteMode by viewModel.isRouteMode.collectAsState()
    val selectedRouteLocations by viewModel.selectedRouteLocations.collectAsState()
    val routeWaypoints by viewModel.routeWaypoints.collectAsState()
    val currentRoute by viewModel.currentRoute.collectAsState()
    var currentScreen by remember { mutableStateOf(Screen.MAP) }

    when {
        isAddingLocation -> {
            com.activemap.shared.ui.SharedAddLocationForm(
                onSave = { location -> viewModel.saveLocation(location) { viewModel.cancelAddingLocation() } },
                onCancel = { viewModel.cancelAddingLocation() },
                modifier = Modifier.fillMaxSize(),
                initialLat = pickedPoint?.first,
                initialLng = pickedPoint?.second
            )
        }
        selectedLocation != null -> {
            com.activemap.shared.ui.SharedLocationDetail(
                location = selectedLocation!!,
                onBack = { viewModel.selectLocation(null) },
                onUpdate = { viewModel.updateLocation(it) },
                onDelete = { viewModel.deleteLocation(it.id) },
                modifier = Modifier.fillMaxSize()
            )
        }
        else -> {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize()) {
                    TopAppBar(
                        title = { Text("ActiveMap") },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        actions = {
                            IconButton(onClick = { viewModel.toggleRouteMode() }) {
                                Icon(Icons.Default.LocationOn, contentDescription = "Route",
                                    tint = if (isRouteMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                        }
                    )
                    when (currentScreen) {
                        Screen.MAP -> {
                            MapViewDesktop(
                                locations = locations,
                                onLocationClick = { loc ->
                                    if (isRouteMode) viewModel.toggleRouteLocation(loc)
                                    else viewModel.selectLocation(loc)
                                },
                                onMapClick = { lat, lng ->
                                    if (isRouteMode) viewModel.setRoutePoint(lat, lng)
                                    else viewModel.startAddingLocationAt(lat, lng)
                                },
                                isRouteMode = isRouteMode,
                                routeWaypoints = routeWaypoints,
                                selectedRouteLocations = selectedRouteLocations.map { it.latitude to it.longitude },
                                pickedPoint = pickedPoint,
                                currentRoute = currentRoute,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Screen.LIST -> {
                            SharedLocationList(
                                locations = locations,
                                filter = currentFilter,
                                onFilterChange = { viewModel.updateFilter(it) },
                                onLocationClick = { viewModel.selectLocation(it) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Screen.HISTORY -> {
                            SharedHistoryScreen(
                                viewModel = viewModel,
                                onTrackClick = { track -> }
                            )
                        }
                    }
                    NavigationBar {
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                            label = { Text("Карта") },
                            selected = currentScreen == Screen.MAP,
                            onClick = { currentScreen = Screen.MAP }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.List, contentDescription = null) },
                            label = { Text("Список") },
                            selected = currentScreen == Screen.LIST,
                            onClick = { currentScreen = Screen.LIST }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                            label = { Text("История") },
                            selected = currentScreen == Screen.HISTORY,
                            onClick = { currentScreen = Screen.HISTORY }
                        )
                    }
                }
                FloatingActionButton(
                    onClick = { viewModel.startAddingLocation() },
                    modifier = Modifier.padding(16.dp).align(Alignment.BottomEnd),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        }
    }
}
