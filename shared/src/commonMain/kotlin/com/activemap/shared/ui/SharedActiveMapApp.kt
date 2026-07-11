package com.activemap.shared.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.activemap.shared.model.Location
import com.activemap.shared.model.Route
import com.activemap.shared.model.LocationFilter
import com.activemap.shared.viewmodel.LocationViewModel
import com.activemap.shared.resources.Strings
import com.activemap.shared.resources.LocaleManager
import com.activemap.shared.resources.AppLanguage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedActiveMapApp(
    viewModel: LocationViewModel,
    mapView: @Composable (
        locations: List<Location>,
        onLocationClick: (Location) -> Unit,
        onLongPress: (Double, Double) -> Unit,
        isRouteMode: Boolean,
        routeStart: Pair<Double, Double>?,
        routeEnd: Pair<Double, Double>?,
        currentRoute: Route?,
        modifier: Modifier
    ) -> Unit,
    onCenterOnMe: () -> Unit = {}
) {
    val locations by viewModel.locations.collectAsState()
    val selectedLocation by viewModel.selectedLocation.collectAsState()
    val isAddingLocation by viewModel.isAddingLocation.collectAsState()
    val currentFilter by viewModel.currentFilter.collectAsState()
    val pickedLatLng by viewModel.pickedLatLng.collectAsState()
    val isRouteMode by viewModel.isRouteMode.collectAsState()
    val routeStart by viewModel.routeStart.collectAsState()
    val routeEnd by viewModel.routeEnd.collectAsState()
    val currentRoute by viewModel.currentRoute.collectAsState()
    val isCalculatingRoute by viewModel.isCalculatingRoute.collectAsState()
    val routeError by viewModel.routeError.collectAsState()
    val error by viewModel.error.collectAsState()
    val operationSuccess by viewModel.operationSuccess.collectAsState()
    val currentLanguage by LocaleManager.currentLanguage.collectAsState()

    var currentScreen by remember { mutableStateOf(Screen.MAP) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }
    
    LaunchedEffect(operationSuccess) {
        operationSuccess?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSuccess()
        }
    }

    if (showLanguageDialog) {
        SharedLanguageSelector(
            currentLanguage = currentLanguage,
            onLanguageSelected = { language ->
                LocaleManager.setLanguage(language)
                showLanguageDialog = false
            },
            onDismiss = { showLanguageDialog = false }
        )
    }

    when {
        isAddingLocation -> {
            SharedAddLocationForm(
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
            SharedLocationDetail(
                location = selectedLocation!!,
                onBack = { viewModel.selectLocation(null) },
                onUpdate = { viewModel.updateLocation(it) },
                onDelete = { viewModel.deleteLocation(it.id) },
                modifier = Modifier.fillMaxSize()
            )
        }
        else -> {
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    TopAppBar(
                        title = { Text(Strings.appName()) },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        actions = {
                            IconButton(onClick = { viewModel.toggleRouteMode() }) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = Strings.routeMode(),
                                    tint = if (isRouteMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            IconButton(onClick = { showLanguageDialog = true }) {
                                Icon(
                                    Icons.Default.Settings,
                                    contentDescription = Strings.language()
                                )
                            }
                        }
                    )
                },
                floatingActionButton = {
                    Column {
                        FloatingActionButton(
                            onClick = { onCenterOnMe() },
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = Strings.centerOnMe())
                        }
                        if (isRouteMode && (routeStart != null || routeEnd != null)) {
                            FloatingActionButton(
                                onClick = { viewModel.clearRoute() },
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = Strings.clearRoute())
                            }
                        }
                        FloatingActionButton(
                            onClick = { viewModel.startAddingLocation() },
                            containerColor = MaterialTheme.colorScheme.primary
                        ) {
                            Icon(Icons.Default.Add, contentDescription = Strings.addLocation())
                        }
                    }
                },
                bottomBar = {
                    NavigationBar {
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.LocationOn, contentDescription = Strings.map()) },
                            label = { Text(Strings.map()) },
                            selected = currentScreen == Screen.MAP,
                            onClick = { currentScreen = Screen.MAP }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.List, contentDescription = Strings.list()) },
                            label = { Text(Strings.list()) },
                            selected = currentScreen == Screen.LIST,
                            onClick = { currentScreen = Screen.LIST }
                        )
                    }
                }
            ) { paddingValues ->
                when (currentScreen) {
                    Screen.MAP -> {
                        Box(modifier = Modifier.padding(paddingValues)) {
                            mapView(
                                locations,
                                { viewModel.selectLocation(it) },
                                { lat, lng ->
                                    if (isRouteMode) {
                                        viewModel.setRoutePoint(lat, lng)
                                    } else {
                                        viewModel.startAddingLocationAt(lat, lng)
                                    }
                                },
                                isRouteMode,
                                routeStart,
                                routeEnd,
                                currentRoute,
                                Modifier.fillMaxSize()
                            )
                            
                            if (isCalculatingRoute) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .padding(16.dp)
                                )
                            }
                            
                            routeError?.let { error ->
                                Snackbar(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(16.dp)
                                ) {
                                    Text(error)
                                }
                            }
                            
                            currentRoute?.let { route ->
                                Snackbar(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(16.dp)
                                ) {
                                    Text(Strings.routeInfo(route.distanceKm, route.durationText))
                                }
                            }
                        }
                    }
                    Screen.LIST -> {
                        SharedLocationList(
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