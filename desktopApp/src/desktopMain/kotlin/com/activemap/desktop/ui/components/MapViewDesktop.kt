package com.activemap.desktop.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.activemap.shared.model.ActivityType
import com.activemap.shared.model.Location
import com.activemap.shared.model.Route
import com.activemap.shared.resources.Strings

@Composable
fun MapViewDesktop(
    locations: List<Location>,
    onLocationClick: (Location) -> Unit,
    onMapClick: (Double, Double) -> Unit = { _, _ -> },
    isRouteMode: Boolean = false,
    routeWaypoints: List<Pair<Double, Double>> = emptyList(),
    selectedRouteLocations: List<Pair<Double, Double>> = emptyList(),
    pickedPoint: Pair<Double, Double>? = null,
    currentRoute: Route? = null,
    modifier: Modifier = Modifier,
    onZoomIn: () -> Unit = {},
    onZoomOut: () -> Unit = {}
) {
    val noLocationsText = Strings.noLocations()
    var zoomLevel by remember { mutableFloatStateOf(1f) }
    var panOffset by remember { mutableStateOf(Offset(0f, 0f)) }

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clickable { /* Handle map click */ }
        ) {
            drawRect(color = Color(0xFFF0F0F0), topLeft = Offset.Zero, size = size)

            val gridSpacing = 50f
            var x = 0f
            while (x <= size.width) {
                drawLine(color = Color(0xFFE0E0E0), start = Offset(x, 0f), end = Offset(x, size.height), strokeWidth = 1f)
                x += gridSpacing
            }
            var y = 0f
            while (y <= size.height) {
                drawLine(color = Color(0xFFE0E0E0), start = Offset(0f, y), end = Offset(size.width, y), strokeWidth = 1f)
                y += gridSpacing
            }

            val allPoints = mutableListOf<Pair<Double, Double>>()
            locations.forEach { allPoints.add(it.latitude to it.longitude) }
            routeWaypoints.forEach { allPoints.add(it) }
            pickedPoint?.let { allPoints.add(it) }
            currentRoute?.points?.forEach { allPoints.add(it.latitude to it.longitude) }

            if (allPoints.isNotEmpty()) {
                val minLat = allPoints.minOf { it.first }
                val maxLat = allPoints.maxOf { it.first }
                val minLon = allPoints.minOf { it.second }
                val maxLon = allPoints.maxOf { it.second }

                val latRange = (maxLat - minLat).coerceAtLeast(0.001)
                val lonRange = (maxLon - minLon).coerceAtLeast(0.001)

                val padding = 50f / zoomLevel
                val mapWidth = size.width - padding * 2
                val mapHeight = size.height - padding * 2
                val centerX = size.width / 2 + panOffset.x
                val centerY = size.height / 2 + panOffset.y

                fun toScreen(lat: Double, lon: Double): Offset {
                    val sx = centerX + ((lon - (minLon + maxLon) / 2) / lonRange * mapWidth).toFloat() * zoomLevel
                    val sy = centerY - ((lat - (minLat + maxLat) / 2) / latRange * mapHeight).toFloat() * zoomLevel
                    return Offset(sx, sy)
                }

                currentRoute?.let { route ->
                    if (route.points.size >= 2) {
                        for (i in 0 until route.points.size - 1) {
                            val start = toScreen(route.points[i].latitude, route.points[i].longitude)
                            val end = toScreen(route.points[i + 1].latitude, route.points[i + 1].longitude)
                            drawLine(color = Color.Blue, start = start, end = end, strokeWidth = 6f)
                        }
                    }
                }

                routeWaypoints.forEachIndexed { index, waypoint ->
                    val pos = toScreen(waypoint.first, waypoint.second)
                    val color = when (index) {
                        0 -> Color.Green
                        routeWaypoints.lastIndex -> Color.Red
                        else -> Color(0xFFFF9800)
                    }
                    drawCircle(color = color, radius = 12f, center = pos)
                    drawCircle(color = Color.Black, radius = 12f, center = pos, style = Stroke(width = 2f))
                }

                pickedPoint?.let { point ->
                    val pos = toScreen(point.first, point.second)
                    drawCircle(color = Color(0xFF00BCD4), radius = 12f, center = pos)
                    drawCircle(color = Color.Black, radius = 12f, center = pos, style = Stroke(width = 2f))
                }

                locations.forEach { location ->
                    val pos = toScreen(location.latitude, location.longitude)
                    val isSelected = selectedRouteLocations.any {
                        it.first == location.latitude && it.second == location.longitude
                    }
                    val markerColor = when {
                        isRouteMode && isSelected -> Color(0xFFFF9800)
                        else -> when (location.activityType) {
                            ActivityType.SPORT -> Color.Red
                            ActivityType.WORK -> Color.Blue
                            ActivityType.REST -> Color.Green
                            ActivityType.EDUCATION -> Color.Yellow
                            ActivityType.ENTERTAINMENT -> Color.Magenta
                        }
                    }
                    val radius = if (isRouteMode && isSelected) 20f else 15f
                    drawCircle(color = markerColor, radius = radius, center = pos, style = Fill)
                    drawCircle(color = Color.Black, radius = radius, center = pos, style = Stroke(width = 2f))
                }
            } else {
                drawCircle(color = Color.LightGray, radius = 30f, center = Offset(size.width / 2, size.height / 2))
            }
        }

        if (locations.isEmpty()) {
            Text(
                text = noLocationsText,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Column(
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FloatingActionButton(
                onClick = { zoomLevel = (zoomLevel + 0.2f).coerceAtMost(5f); onZoomIn() },
                modifier = Modifier.size(40.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text("+", style = MaterialTheme.typography.titleMedium)
            }
            FloatingActionButton(
                onClick = { zoomLevel = (zoomLevel - 0.2f).coerceAtLeast(0.3f); onZoomOut() },
                modifier = Modifier.size(40.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text("\u2212", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
