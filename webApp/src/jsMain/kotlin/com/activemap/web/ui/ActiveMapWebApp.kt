package com.activemap.web.ui

import androidx.compose.runtime.*
import com.activemap.web.model.*
import com.activemap.web.viewmodel.WebLocationViewModel
import com.activemap.web.ui.components.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

@Composable
fun ActiveMapWebApp(viewModel: WebLocationViewModel) {
    val locations by viewModel.locations.collectAsState()
    val selectedLocation by viewModel.selectedLocation.collectAsState()
    val isAddingLocation by viewModel.isAddingLocation.collectAsState()
    val currentFilter by viewModel.currentFilter.collectAsState()
    val pickedPoint by viewModel.pickedPoint.collectAsState()
    val isRouteMode by viewModel.isRouteMode.collectAsState()
    val selectedRouteLocations by viewModel.selectedRouteLocations.collectAsState()
    val routeWaypoints by viewModel.routeWaypoints.collectAsState()
    val currentRoute by viewModel.currentRoute.collectAsState()
    var currentScreen by remember { mutableStateOf("map") }
    var showLangMenu by remember { mutableStateOf(false) }
    val lang = LocaleState.currentLanguage

    if (isAddingLocation) {
        AddLocationFormWeb(
            onSave = { location -> viewModel.saveLocation(location) { viewModel.cancelAddingLocation() } },
            onCancel = { viewModel.cancelAddingLocation() }
        )
    } else if (selectedLocation != null) {
        LocationDetailWeb(
            location = selectedLocation!!,
            onBack = { viewModel.selectLocation(null) },
            onUpdate = { viewModel.updateLocation(it) },
            onDelete = { viewModel.deleteLocation(it.id) }
        )
    } else {
        Div(attrs = { style { backgroundColor(Color("#ffffff")) } }) {
            // Toolbar
            Div(attrs = { style { property("padding", "12px"); property("background-color", "#1976d2"); property("color", "white") } }) {
                Span(attrs = { style { property("font-size", "20px"); property("font-weight", "bold"); property("margin-right", "16px") } }) { Text(Strings.appName()) }
                Button(attrs = {
                    onClick { currentScreen = "map" }
                    style { property("margin", "0 4px"); property("padding", "6px 12px"); property("background-color", if (currentScreen == "map") "white" else "#1565c0"); property("color", if (currentScreen == "map") "#1976d2" else "white"); property("border", "1px solid #1565c0"); property("border-radius", "4px"); property("cursor", "pointer") }
                }) { Text(Strings.map()) }
                Button(attrs = {
                    onClick { currentScreen = "list" }
                    style { property("margin", "0 4px"); property("padding", "6px 12px"); property("background-color", if (currentScreen == "list") "white" else "#1565c0"); property("color", if (currentScreen == "list") "#1976d2" else "white"); property("border", "1px solid #1565c0"); property("border-radius", "4px"); property("cursor", "pointer") }
                }) { Text(Strings.list()) }
                Button(attrs = {
                    onClick { viewModel.toggleRouteMode() }
                    style { property("margin", "0 4px"); property("padding", "6px 12px"); property("background-color", if (isRouteMode) "#ff9800" else "#1565c0"); property("color", "white"); property("border", "1px solid #1565c0"); property("border-radius", "4px"); property("cursor", "pointer") }
                }) { Text(Strings.route()) }
                Button(attrs = {
                    onClick { viewModel.startAddingLocationAt(0.0, 0.0) }
                    style { property("margin", "0 4px"); property("padding", "6px 12px"); property("background-color", "#4caf50"); property("color", "white"); property("border", "1px solid #388e3c"); property("border-radius", "4px"); property("cursor", "pointer") }
                }) { Text(Strings.addNew()) }

                Span(attrs = { style { property("margin-left", "8px"); property("cursor", "pointer") } }) {
                    Text(lang.displayName)
                }
            }

            // Content
            if (currentScreen == "map") {
                MapViewWeb(
                    locations = locations,
                    onLocationClick = { location ->
                        if (isRouteMode) viewModel.toggleRouteLocation(location)
                        else viewModel.selectLocation(location)
                    },
                    onMapClick = { lat, lng ->
                        if (isRouteMode) viewModel.setRoutePoint(lat, lng)
                        else viewModel.startAddingLocationAt(lat, lng)
                    },
                    isRouteMode = isRouteMode,
                    routeWaypoints = routeWaypoints,
                    selectedRouteLocations = selectedRouteLocations.map { it.latitude to it.longitude },
                    pickedPoint = pickedPoint,
                    currentRoute = currentRoute
                )
            } else {
                LocationListWeb(
                    locations = locations,
                    filter = currentFilter,
                    onFilterChange = { viewModel.updateFilter(it) },
                    onLocationClick = { viewModel.selectLocation(it) }
                )
            }
        }
    }
}
