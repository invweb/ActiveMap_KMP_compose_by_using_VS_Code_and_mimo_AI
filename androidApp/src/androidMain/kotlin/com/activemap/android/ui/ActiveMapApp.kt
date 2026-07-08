package com.activemap.android.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.activemap.shared.viewmodel.LocationViewModel
import com.activemap.android.ui.components.LocationList
import com.activemap.android.ui.components.LocationDetail
import com.activemap.android.ui.components.AddLocationForm
import com.activemap.android.ui.components.MapView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveMapApp(viewModel: LocationViewModel) {
    val locations by viewModel.locations.collectAsState()
    val selectedLocation by viewModel.selectedLocation.collectAsState()
    val isAddingLocation by viewModel.isAddingLocation.collectAsState()
    val currentFilter by viewModel.currentFilter.collectAsState()
    val pickedLatLng by viewModel.pickedLatLng.collectAsState()

    var currentScreen by remember { mutableStateOf(Screen.MAP) }

    when {
        isAddingLocation -> {
            AddLocationForm(
                onSave = { location ->
                    viewModel.saveLocation(location) {
                        viewModel.cancelAddingLocation()
                    }
                },
                onCancel = { viewModel.cancelAddingLocation() },
                modifier = Modifier.fillMaxSize(),
                initialLat = pickedLatLng?.first,
                initialLng = pickedLatLng?.second
            )
        }
        selectedLocation != null -> {
            LocationDetail(
                location = selectedLocation!!,
                onBack = { viewModel.selectLocation(null) },
                onUpdate = { viewModel.updateLocation(it) },
                onDelete = { viewModel.deleteLocation(it.id) },
                modifier = Modifier.fillMaxSize()
            )
        }
        else -> {
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
                    FloatingActionButton(
                        onClick = { viewModel.startAddingLocation() },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Добавить локацию")
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
                when (currentScreen) {
                    Screen.MAP -> {
                        MapView(
                            locations = locations,
                            onLocationClick = { viewModel.selectLocation(it) },
                            onLongPress = { lat, lng -> viewModel.startAddingLocationAt(lat, lng) },
                            modifier = Modifier.padding(paddingValues)
                        )
                    }
                    Screen.LIST -> {
                        LocationList(
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
