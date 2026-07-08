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

@Composable
fun MapViewDesktop(
    locations: List<Location>,
    onLocationClick: (Location) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Simple map visualization using Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .clickable { /* Handle map click */ }
        ) {
            // Draw background (light gray for map)
            drawRect(
                color = Color(0xFFF0F0F0),
                topLeft = Offset.Zero,
                size = size
            )
            
            // Draw grid lines for reference
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
            
            // Calculate bounds for all locations
            if (locations.isNotEmpty()) {
                val minLat = locations.minOf { it.latitude }
                val maxLat = locations.maxOf { it.latitude }
                val minLon = locations.minOf { it.longitude }
                val maxLon = locations.maxOf { it.longitude }
                
                val latRange = maxLat - minLat
                val lonRange = maxLon - minLon
                
                // Add padding
                val padding = 50f
                val mapWidth = size.width - padding * 2
                val mapHeight = size.height - padding * 2
                
                // Draw markers
                locations.forEach { location ->
                    val x = padding + ((location.longitude - minLon) / lonRange * mapWidth).toFloat()
                    val y = padding + ((maxLat - location.latitude) / latRange * mapHeight).toFloat()
                    
                    // Draw marker based on activity type
                    val markerColor = when (location.activityType) {
                        ActivityType.SPORT -> Color.Red
                        ActivityType.WORK -> Color.Blue
                        ActivityType.REST -> Color.Green
                        ActivityType.EDUCATION -> Color.Yellow
                        ActivityType.ENTERTAINMENT -> Color.Magenta
                    }
                    
                    // Draw circle marker
                    drawCircle(
                        color = markerColor,
                        radius = 15f,
                        center = Offset(x, y),
                        style = Fill
                    )
                    
                    // Draw marker border
                    drawCircle(
                        color = Color.Black,
                        radius = 15f,
                        center = Offset(x, y),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
                    )
                }
            } else {
                // Draw "No locations" text
                drawContext.canvas.nativeCanvas.apply {
                    val paint = android.graphics.Paint().apply {
                        color = android.graphics.Color.GRAY
                        textSize = 48f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                    drawText(
                        "Нет локаций",
                        size.width / 2,
                        size.height / 2,
                        paint
                    )
                }
            }
        }
        
        // "My location" button
        FloatingActionButton(
            onClick = {
                // TODO: Get actual location
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = "Центр на мне")
        }
        
        // Legend
        Card(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = "Легенда:",
                    style = MaterialTheme.typography.titleSmall
                )
                LegendItem("Спорт", Color.Red)
                LegendItem("Работа", Color.Blue)
                LegendItem("Отдых", Color.Green)
                LegendItem("Образование", Color.Yellow)
                LegendItem("Развлечения", Color.Magenta)
            }
        }
        
        // Click handler overlay (simplified)
        // In a real app, you would implement proper click detection
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
