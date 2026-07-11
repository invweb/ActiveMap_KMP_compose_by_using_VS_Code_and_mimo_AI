package com.activemap.android.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.activemap.shared.viewmodel.LocationViewModel
import com.activemap.shared.ui.SharedActiveMapApp
import com.activemap.android.ui.components.MapView

@Composable
fun ActiveMapApp(
    viewModel: LocationViewModel,
    onRequestLocationPermission: () -> Unit = {},
    onExportData: (suspend (String) -> Unit)? = null,
    onImportData: (suspend () -> String?)? = null
) {
    var osmMapView by remember { mutableStateOf<org.osmdroid.views.MapView?>(null) }

    SharedActiveMapApp(
        viewModel = viewModel,
        mapView = { locations, onLocationClick, onLongPress, isRouteMode, routeWaypoints, selectedRouteLocations, pickedPoint, currentRoute, modifier ->
            MapView(
                locations = locations,
                onLocationClick = onLocationClick,
                onLongPress = onLongPress,
                isRouteMode = isRouteMode,
                routeWaypoints = routeWaypoints,
                selectedRouteLocations = selectedRouteLocations,
                pickedPoint = pickedPoint,
                currentRoute = currentRoute,
                onMapReady = { osmMapView = it },
                modifier = modifier
            )
        },
        onCenterOnMe = { onRequestLocationPermission() },
        onZoomIn = { osmMapView?.controller?.zoomIn() },
        onZoomOut = { osmMapView?.controller?.zoomOut() },
        onExportData = onExportData,
        onImportData = onImportData
    )
}
