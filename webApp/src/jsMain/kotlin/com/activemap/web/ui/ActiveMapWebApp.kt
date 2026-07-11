package com.activemap.web.ui

import androidx.compose.runtime.*
import com.activemap.shared.viewmodel.LocationViewModel
import com.activemap.shared.ui.SharedActiveMapApp
import com.activemap.web.ui.components.MapViewWeb
import kotlinx.browser.document
import kotlinx.coroutines.suspendCancellableCoroutine
import org.w3c.files.FileReader
import kotlin.coroutines.resume

@Composable
fun ActiveMapWebApp(viewModel: LocationViewModel) {
    SharedActiveMapApp(
        viewModel = viewModel,
        mapView = { locations, onLocationClick, onLongPress, isRouteMode, routeWaypoints, pickedPoint, currentRoute, modifier ->
            MapViewWeb(
                locations = locations,
                onLocationClick = onLocationClick,
                onMapClick = { x, y -> onLongPress(x, y) },
                isRouteMode = isRouteMode,
                routeWaypoints = routeWaypoints,
                pickedPoint = pickedPoint,
                currentRoute = currentRoute
            )
        },
        onCenterOnMe = { viewModel.centerOnMe() },
        onExportData = { json ->
            downloadJsonFile(json, "activemap_export.json")
        },
        onImportData = {
            pickAndReadJsonFile()
        }
    )
}

private fun downloadJsonFile(json: String, filename: String) {
    val blob = js("new Blob([json], {type: 'application/json'})")
    val url = js("URL.createObjectURL(blob)")
    val a = js("document.createElement('a')")
    js("a.href = url")
    js("a.download = filename")
    js("a.click()")
    js("URL.revokeObjectURL(url)")
}

private suspend fun pickAndReadJsonFile(): String? = suspendCancellableCoroutine { continuation ->
    val input = document.createElement("input").apply {
        asDynamic().type = "file"
        asDynamic().accept = ".json"
        asDynamic().style.display = "none"
    }
    document.body?.appendChild(input)

    val reader = FileReader()
    reader.onload = {
        val result = reader.result as? String
        document.body?.removeChild(input)
        continuation.resume(result)
    }

    input.asDynamic().onchange = {
        val file = input.asDynamic().files[0] as? dynamic
        if (file != null && file != undefined) {
            reader.readAsText(file)
        } else {
            document.body?.removeChild(input)
            continuation.resume(null)
        }
    }

    input.asDynamic().click()
}
