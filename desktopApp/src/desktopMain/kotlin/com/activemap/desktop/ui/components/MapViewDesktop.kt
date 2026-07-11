package com.activemap.desktop.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
    var zoomLevel by remember { mutableStateOf(1f) }

    Box(modifier = modifier.fillMaxSize().background(Color(0xFFE3F2FD))) {
        Canvas(
            modifier = Modifier.fillMaxSize().clickable { }
        ) {
            drawRect(color = Color(0xFFBBDEFB), topLeft = Offset.Zero, size = size)

            val gridSpacing = 50f
            var gx = 0f
            while (gx <= size.width) {
                drawLine(color = Color(0xFF90CAF9), start = Offset(gx, 0f), end = Offset(gx, size.height), strokeWidth = 1f)
                gx += gridSpacing
            }
            var gy = 0f
            while (gy <= size.height) {
                drawLine(color = Color(0xFF90CAF9), start = Offset(0f, gy), end = Offset(size.width, gy), strokeWidth = 1f)
                gy += gridSpacing
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
                val cx = size.width / 2
                val cy = size.height / 2

                fun toScreen(lat: Double, lon: Double): Offset {
                    val sx = cx + ((lon - (minLon + maxLon) / 2) / lonRange * (size.width - 100)).toFloat() * zoomLevel
                    val sy = cy - ((lat - (minLat + maxLat) / 2) / latRange * (size.height - 100)).toFloat() * zoomLevel
                    return Offset(sx, sy)
                }

                currentRoute?.let { route ->
                    if (route.points.size >= 2) {
                        for (i in 0 until route.points.size - 1) {
                            val s = toScreen(route.points[i].latitude, route.points[i].longitude)
                            val e = toScreen(route.points[i + 1].latitude, route.points[i + 1].longitude)
                            drawLine(color = Color(0xFF1565C0), start = s, end = e, strokeWidth = 6f)
                        }
                    }
                }

                routeWaypoints.forEachIndexed { index, wp ->
                    val pos = toScreen(wp.first, wp.second)
                    val color = when (index) {
                        0 -> Color(0xFF4CAF50); routeWaypoints.lastIndex -> Color(0xFFF44336); else -> Color(0xFFFF9800)
                    }
                    drawCircle(color = color, radius = 12f, center = pos)
                    drawCircle(color = Color.Black, radius = 12f, center = pos, style = Stroke(width = 2f))
                }

                pickedPoint?.let { pt ->
                    val pos = toScreen(pt.first, pt.second)
                    drawCircle(color = Color(0xFF00BCD4), radius = 12f, center = pos)
                    drawCircle(color = Color.Black, radius = 12f, center = pos, style = Stroke(width = 2f))
                }

                locations.forEach { loc ->
                    val pos = toScreen(loc.latitude, loc.longitude)
                    val sel = selectedRouteLocations.any { it.first == loc.latitude && it.second == loc.longitude }
                    val c = when {
                        isRouteMode && sel -> Color(0xFFFF9800)
                        else -> when (loc.activityType) {
                            ActivityType.SPORT -> Color(0xFFF44336); ActivityType.WORK -> Color(0xFF2196F3)
                            ActivityType.REST -> Color(0xFF4CAF50); ActivityType.EDUCATION -> Color(0xFFFFEB3B)
                            ActivityType.ENTERTAINMENT -> Color(0xFF9C27B0)
                        }
                    }
                    val r = if (isRouteMode && sel) 20f else 15f
                    drawCircle(color = c, radius = r, center = pos, style = Fill)
                    drawCircle(color = Color.Black, radius = r, center = pos, style = Stroke(width = 2f))
                }
            }
        }

        if (locations.isEmpty()) {
            Text(text = noLocationsText, style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.align(Alignment.Center))
        }

        Column(modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            FloatingActionButton(onClick = { zoomLevel = (zoomLevel + 0.2f).coerceAtMost(5f); onZoomIn() },
                modifier = Modifier.size(40.dp), containerColor = MaterialTheme.colorScheme.primaryContainer) {
                Text("+", style = MaterialTheme.typography.titleMedium)
            }
            FloatingActionButton(onClick = { zoomLevel = (zoomLevel - 0.2f).coerceAtLeast(0.3f); onZoomOut() },
                modifier = Modifier.size(40.dp), containerColor = MaterialTheme.colorScheme.primaryContainer) {
                Text("\u2212", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
