package com.activemap.desktop.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.activemap.shared.viewmodel.LocationViewModel
import com.activemap.shared.ui.SharedActiveMapApp
import com.activemap.desktop.ui.components.MapViewDesktop

@Composable
fun ActiveMapDesktopApp(viewModel: LocationViewModel) {
    SharedActiveMapApp(
        viewModel = viewModel,
        mapView = { locations, onLocationClick, onLongPress, isRouteMode, routeStart, routeEnd, currentRoute, modifier ->
            MapViewDesktop(
                locations = locations,
                onLocationClick = onLocationClick,
                onMapClick = onLongPress,
                isRouteMode = isRouteMode,
                routeStart = routeStart,
                routeEnd = routeEnd,
                currentRoute = currentRoute,
                modifier = modifier
            )
        },
        onCenterOnMe = { viewModel.centerOnMe() }
    )
}