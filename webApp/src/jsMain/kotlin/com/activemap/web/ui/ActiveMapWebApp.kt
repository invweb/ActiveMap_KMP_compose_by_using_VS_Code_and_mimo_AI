package com.activemap.web.ui

import androidx.compose.runtime.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import com.activemap.shared.viewmodel.LocationViewModel
import com.activemap.shared.ui.SharedActiveMapApp
import com.activemap.web.ui.components.MapViewWeb

@Composable
fun ActiveMapWebApp(viewModel: LocationViewModel) {
    SharedActiveMapApp(
        viewModel = viewModel,
        mapView = { locations, onLocationClick, onLongPress, isRouteMode, routeStart, routeEnd, currentRoute, modifier ->
            MapViewWeb(
                locations = locations,
                onLocationClick = onLocationClick,
                onMapClick = { x, y -> onLongPress(x, y) },
                isRouteMode = isRouteMode,
                routeStart = routeStart,
                routeEnd = routeEnd,
                currentRoute = currentRoute
            )
        },
        onCenterOnMe = { viewModel.centerOnMe() }
    )
}