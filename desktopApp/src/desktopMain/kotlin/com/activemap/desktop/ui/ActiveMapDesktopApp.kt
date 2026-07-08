package com.activemap.desktop.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.activemap.shared.model.Location
import com.activemap.shared.viewmodel.LocationViewModel
import com.activemap.desktop.ui.components.LocationListDesktop
import com.activemap.desktop.ui.components.LocationDetailDesktop
import com.activemap.desktop.ui.components.AddLocationFormDesktop
import com.activemap.desktop.ui.components.MapViewDesktop

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveMapDesktopApp(viewModel: LocationViewModel) {
    val locations by viewModel.locations.collectAsState()
    val selectedLocation by viewModel.selectedLocation.collectAsState()
    val isAddingLocation by viewModel.isAddingLocation.collectAsState()
    val currentFilter by viewModel.currentFilter.collectAsState()
    
    var currentScreen by remember { mutableStateOf(Screen.MAP) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Active Map") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            if (!isAddingLocation) {
                FloatingActionButton(
                    onClick = { viewModel.startAddingLocation() },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Добавить локацию")
                }
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Place, contentDescription = "Карта") },
                    label = { Text("Карта") },
                    selected = currentScreen == Screen.MAP,
                    onClick = { currentScreen = Screen.MAP }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = "Список") },
                    label = { Text("Список") },
                    selected = currentScreen == Screen.LIST,
                    onClick = { currentScreen = Screen.LIST }
                )
            }
        }
    ) { paddingValues ->
        when {
            isAddingLocation -> {
                AddLocationFormDesktop(
                    onSave = { location ->
                        viewModel.saveLocation(location) {
                            viewModel.cancelAddingLocation()
                        }
                    },
                    onCancel = { viewModel.cancelAddingLocation() },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            selectedLocation != null -> {
                LocationDetailDesktop(
                    location = selectedLocation!!,
                    onBack = { viewModel.selectLocation(null) },
                    onUpdate = { viewModel.updateLocation(it) },
                    onDelete = { viewModel.deleteLocation(it.id) },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            else -> {
                when (currentScreen) {
                    Screen.MAP -> {
                        MapViewDesktop(
                            locations = locations,
                            onLocationClick = { viewModel.selectLocation(it) },
                            modifier = Modifier.padding(paddingValues)
                        )
                    }
                    Screen.LIST -> {
                        LocationListDesktop(
                            locations = locations,
                            filter = currentFilter,
                            onFilterChange = { viewModel.updateFilter(it) },
                            onLocationClick = { viewModel.selectLocation(it) },
                            modifier = Modifier.padding(paddingValues)
                        )
                    }
                }
            }
        }
    }
}

enum class Screen {
    MAP, LIST
}
