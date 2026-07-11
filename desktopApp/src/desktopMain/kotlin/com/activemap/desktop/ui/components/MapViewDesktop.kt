package com.activemap.desktop.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.nativeCanvas
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
    routeStart: Pair<Double, Double>? = null,
    routeEnd: Pair<Double, Double>? = null,
    currentRoute: Route? = null,
    modifier: Modifier = Modifier
) {
    val noLocationsText = Strings.noLocations()
    val centerOnMeText = Strings.centerOnMe()
    val legendText = Strings.legend()
    val sportText = Strings.activitySport()
    val workText = Strings.activityWork()
    val restText = Strings.activityRest()
    val educationText = Strings.activityEducation()
    val entertainmentText = Strings.activityEntertainment()
    val routeStartText = Strings.routeStartMarker()
    val routeEndText = Strings.routeEndMarker()
    val routeText = Strings.routeLine()
    Box(modifier = modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clickable { /* Handle map click */ }
        ) {
            drawRect(
                color = Color(0xFFF0F0F0),
                topLeft = Offset.Zero,
                size = size
            )
            
            val gridSpacing = 50.dp.toPx()
            for (x in 0..size.width.toInt() step gridSpacing.toInt()) {
                drawLine(
                    color = Color(0xFFE0E0E0),
                    start = Offset(x.toFloat(), 0f),
                    end = Offset(x.toFloat(), size.height),
                    strokeWidth = 1f
                )
            }
            for (y in 0..size.height.toInt() step gridSpacing.toInt()) {
                drawLine(
                    color = Color(0xFFE0E0E0),
                    start = Offset(0f, y.toFloat()),
                    end = Offset(size.width, y.toFloat()),
                    strokeWidth = 1f
                )
            }
            
            val allPoints = mutableListOf<Pair<Double, Double>>()
            locations.forEach { allPoints.add(it.latitude to it.longitude) }
            routeStart?.let { allPoints.add(it) }
            routeEnd?.let { allPoints.add(it) }
            currentRoute?.points?.forEach { allPoints.add(it.latitude to it.longitude) }
            
            if (allPoints.isNotEmpty()) {
                val minLat = allPoints.minOf { it.first }
                val maxLat = allPoints.maxOf { it.first }
                val minLon = allPoints.minOf { it.second }
                val maxLon = allPoints.maxOf { it.second }
                
                val latRange = (maxLat - minLat).coerceAtLeast(0.001)
                val lonRange = (maxLon - minLon).coerceAtLeast(0.001)
                
                val padding = 50f
                val mapWidth = size.width - padding * 2
                val mapHeight = size.height - padding * 2
                
                fun toScreen(lat: Double, lon: Double): Offset {
                    val x = padding + ((lon - minLon) / lonRange * mapWidth).toFloat()
                    val y = padding + ((maxLat - lat) / latRange * mapHeight).toFloat()
                    return Offset(x, y)
                }
                
                currentRoute?.let { route ->
                    if (route.points.size >= 2) {
                        for (i in 0 until route.points.size - 1) {
                            val start = toScreen(route.points[i].latitude, route.points[i].longitude)
                            val end = toScreen(route.points[i + 1].latitude, route.points[i + 1].longitude)
                            drawLine(
                                color = Color.Blue,
                                start = start,
                                end = end,
                                strokeWidth = 6f
                            )
                        }
                    }
                }
                
                routeStart?.let { start ->
                    val pos = toScreen(start.first, start.second)
                    drawCircle(color = Color.Green, radius = 12f, center = pos)
                    drawCircle(color = Color.Black, radius = 12f, center = pos, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f))
                }
                
                routeEnd?.let { end ->
                    val pos = toScreen(end.first, end.second)
                    drawCircle(color = Color.Red, radius = 12f, center = pos)
                    drawCircle(color = Color.Black, radius = 12f, center = pos, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f))
                }
                
                locations.forEach { location ->
                    val pos = toScreen(location.latitude, location.longitude)
                    
                    val markerColor = when (location.activityType) {
                        ActivityType.SPORT -> Color.Red
                        ActivityType.WORK -> Color.Blue
                        ActivityType.REST -> Color.Green
                        ActivityType.EDUCATION -> Color.Yellow
                        ActivityType.ENTERTAINMENT -> Color.Magenta
                    }
                    
                    drawCircle(
                        color = markerColor,
                        radius = 15f,
                        center = pos,
                        style = Fill
                    )
                    
                    drawCircle(
                        color = Color.Black,
                        radius = 15f,
                        center = pos,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
                    )
                }
            } else {
                drawCircle(
                    color = Color.LightGray,
                    radius = 30f,
                    center = Offset(size.width / 2, size.height / 2)
                )
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
        
        FloatingActionButton(
            onClick = { },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = centerOnMeText)
        }
        
        Card(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = legendText,
                    style = MaterialTheme.typography.titleSmall
                )
                LegendItem(sportText, Color.Red)
                LegendItem(workText, Color.Blue)
                LegendItem(restText, Color.Green)
                LegendItem(educationText, Color.Yellow)
                LegendItem(entertainmentText, Color.Magenta)
                if (isRouteMode) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LegendItem(routeStartText, Color.Green)
                    LegendItem(routeEndText, Color.Red)
                    LegendItem(routeText, Color.Blue)
                }
            }
        }
    }
}

@Composable
fun LegendItem(text: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Canvas(
            modifier = Modifier.size(12.dp)
        ) {
            drawCircle(color = color)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
