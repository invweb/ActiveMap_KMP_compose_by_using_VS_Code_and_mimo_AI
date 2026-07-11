package com.activemap.desktop.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.activemap.shared.viewmodel.LocationViewModel
import com.activemap.shared.ui.SharedActiveMapApp
import com.activemap.desktop.ui.components.MapViewDesktop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

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
        onCenterOnMe = { viewModel.centerOnMe() },
        onExportData = { json ->
            withContext(Dispatchers.IO) {
                val chooser = JFileChooser().apply {
                    selectedFile = java.io.File("activemap_export.json")
                    fileFilter = FileNameExtensionFilter("JSON files", "json")
                }
                if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    var file = chooser.selectedFile
                    if (!file.name.endsWith(".json")) {
                        file = java.io.File(file.absolutePath + ".json")
                    }
                    file.writeText(json)
                }
            }
        },
        onImportData = {
            withContext(Dispatchers.IO) {
                val chooser = JFileChooser().apply {
                    fileFilter = FileNameExtensionFilter("JSON files", "json")
                }
                if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    chooser.selectedFile.readText()
                } else {
                    null
                }
            }
        }
    )
}