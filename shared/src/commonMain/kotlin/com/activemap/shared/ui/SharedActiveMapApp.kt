package com.activemap.shared.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.DateRange
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
import kotlinx.coroutines.launch
import com.activemap.shared.ui.SharedHistoryScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedActiveMapApp(
    viewModel: LocationViewModel,
    mapView: @Composable (
        locations: List<Location>,
        onLocationClick: (Location) -> Unit,
        onLongPress: (Double, Double) -> Unit,
        isRouteMode: Boolean,
        routeWaypoints: List<Pair<Double, Double>>,
        selectedRouteLocations: List<Pair<Double, Double>>,
        pickedPoint: Pair<Double, Double>?,
        currentRoute: Route?,
        modifier: Modifier
    ) -> Unit,
    onCenterOnMe: () -> Unit = {},
    onZoomIn: () -> Unit = {},
    onZoomOut: () -> Unit = {},
    onExportData: (suspend (String) -> Unit)? = null,
    onImportData: (suspend () -> String?)? = null
) {
    val locations by viewModel.locations.collectAsState()
    val selectedLocation by viewModel.selectedLocation.collectAsState()
    val isAddingLocation by viewModel.isAddingLocation.collectAsState()
    val currentFilter by viewModel.currentFilter.collectAsState()
    val pickedLatLng by viewModel.pickedLatLng.collectAsState()
    val pickedPoint by viewModel.pickedPoint.collectAsState()
    val isRouteMode by viewModel.isRouteMode.collectAsState()
    val selectedRouteLocations by viewModel.selectedRouteLocations.collectAsState()
    val routeWaypoints by viewModel.routeWaypoints.collectAsState()
    val currentRoute by viewModel.currentRoute.collectAsState()
    val isCalculatingRoute by viewModel.isCalculatingRoute.collectAsState()
    val routeError by viewModel.routeError.collectAsState()
    val error by viewModel.error.collectAsState()
    val operationSuccess by viewModel.operationSuccess.collectAsState()
    val currentLanguage by LocaleManager.currentLanguage.collectAsState()

    var currentScreen by remember { mutableStateOf(Screen.MAP) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
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
                            if (onExportData != null || onImportData != null) {
                                Box {
                                    IconButton(onClick = { showMenu = true }) {
                                        Icon(
                                            Icons.Default.Settings,
                                            contentDescription = Strings.settings()
                                        )
                                    }
                                    DropdownMenu(
                                        expanded = showMenu,
                                        onDismissRequest = { showMenu = false }
                                    ) {
                                        if (onExportData != null) {
                                            DropdownMenuItem(
                                                text = { Text(Strings.export()) },
                                                onClick = {
                                                    showMenu = false
                                                    scope.launch {
                                                        try {
                                                            val data = viewModel.exportData()
                                                            if (data.isNotEmpty()) {
                                                                onExportData(data)
                                                            }
                                                        } catch (_: Exception) {
                                                        }
                                                    }
                                                }
                                            )
                                        }
                                        if (onImportData != null) {
                                            DropdownMenuItem(
                                                text = { Text(Strings.importData()) },
                                                onClick = {
                                                    showMenu = false
                                                    scope.launch {
                                                        try {
                                                            val json = onImportData()
                                                            if (json != null) {
                                                                val count = viewModel.importData(json)
                                                                snackbarHostState.showSnackbar("Imported: $count")
                                                            }
                                                        } catch (_: Exception) {
                                                        }
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            }
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
                            IconButton(onClick = { currentScreen = Screen.HISTORY }) {
                                Icon(
                                    Icons.Default.DateRange,
                                    contentDescription = Strings.history()
                                )
                            }
                        }
                    )
                },
                floatingActionButton = {
                    Column(horizontalAlignment = Alignment.End) {
                        if (isRouteMode && routeWaypoints.isNotEmpty()) {
                            FloatingActionButton(
                                onClick = { viewModel.clearRoute() },
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = Strings.clearRoute())
                            }
                        }
                        SmallFloatingActionButton(
                            onClick = { onZoomIn() },
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Zoom in")
                        }
                        SmallFloatingActionButton(
                            onClick = { onZoomOut() },
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Text("−", style = MaterialTheme.typography.titleLarge)
                        }
                        FloatingActionButton(
                            onClick = { onCenterOnMe() },
                            containerColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = Strings.centerOnMe())
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
                            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = Strings.list()) },
                            label = { Text(Strings.list()) },
                            selected = currentScreen == Screen.LIST,
                            onClick = { currentScreen = Screen.LIST }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.DateRange, contentDescription = Strings.history()) },
                            label = { Text(Strings.history()) },
                            selected = currentScreen == Screen.HISTORY,
                            onClick = { currentScreen = Screen.HISTORY }
                        )
                    }
                }
            ) { paddingValues ->
                when (currentScreen) {
                    Screen.MAP -> {
                        Box(modifier = Modifier.padding(paddingValues)) {
                            mapView(
                                locations,
                                { location ->
                                    if (isRouteMode) {
                                        viewModel.toggleRouteLocation(location)
                                    } else {
                                        viewModel.selectLocation(location)
                                    }
                                },
                                { lat, lng ->
                                    if (isRouteMode) {
                                        viewModel.setRoutePoint(lat, lng)
                                    } else {
                                        viewModel.startAddingLocationAt(lat, lng)
                                    }
                                },
                                isRouteMode,
                                routeWaypoints,
                                selectedRouteLocations.map { it.latitude to it.longitude },
                                pickedPoint,
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
                            
                            if (isRouteMode && selectedRouteLocations.isNotEmpty() && currentRoute == null) {
                                Card(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(16.dp)
                                        .fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = Strings.selectedPoints(selectedRouteLocations.size),
                                            style = MaterialTheme.typography.titleSmall,
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )
                                        Text(
                                            text = selectedRouteLocations.joinToString(" → ") { it.name },
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(bottom = 12.dp)
                                        )
                                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                            OutlinedButton(
                                                onClick = { viewModel.clearSelectedLocations() }
                                            ) {
                                                Text(Strings.cancel())
                                            }
                                            Button(
                                                onClick = { viewModel.buildRouteFromSelected() },
                                                enabled = selectedRouteLocations.size >= 2
                                            ) {
                                                Text(Strings.buildRoute())
                                            }
                                        }
                                    }
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
                    Screen.HISTORY -> {
                        SharedHistoryScreen(
                            viewModel = viewModel,
                            onTrackClick = { track ->
                                // TODO: Show track details
                            }
                        )
                    }
                }
            }
        }
    }
}