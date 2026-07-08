package com.activemap.web.ui.components

import androidx.compose.runtime.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import com.activemap.shared.model.ActivityType
import com.activemap.shared.model.Location

@Composable
fun MapViewWeb(
    locations: List<Location>,
    onLocationClick: (Location) -> Unit
) {
    Div(
        attrs = {
            style {
                display(DisplayStyle.Flex)
                flexDirection(FlexDirection.Column)
                height(100.percent)
                width(100.percent)
            }
        }
    ) {
        // Map container
        Div(
            attrs = {
                style {
                    flex(1)
                    position(Position.Relative)
                    backgroundColor(Color("#f0f0f0"))
                    overflow("hidden")
                }
            }
        ) {
            // Simple map visualization
            if (locations.isEmpty()) {
                Div(
                    attrs = {
                        style {
                            display(DisplayStyle.Flex)
                            justifyContent(JustifyContent.Center)
                            alignItems(AlignItems.Center)
                            height(100.percent)
                            color(Color("#666"))
                            fontSize(24.px)
                        }
                    }
                ) {
                    Text("Нет локаций")
                }
            } else {
                // Calculate bounds
                val minLat = locations.minOf { it.latitude }
                val maxLat = locations.maxOf { it.latitude }
                val minLon = locations.minOf { it.longitude }
                val maxLon = locations.maxOf { it.longitude }
                
                val latRange = maxLat - minLat
                val lonRange = maxLon - minLon
                
                // Draw markers as positioned elements
                locations.forEach { location ->
                    val x = ((location.longitude - minLon) / lonRange * 100).coerceIn(5.0, 95.0)
                    val y = ((maxLat - location.latitude) / latRange * 100).coerceIn(5.0, 95.0)
                    
                    val markerColor = when (location.activityType) {
                        ActivityType.SPORT -> "#f44336"
                        ActivityType.WORK -> "#2196f3"
                        ActivityType.REST -> "#4caf50"
                        ActivityType.EDUCATION -> "#ffeb3b"
                        ActivityType.ENTERTAINMENT -> "#9c27b0"
                    }
                    
                    Div(
                        attrs = {
                            onClick { onLocationClick(location) }
                            style {
                                position(Position.Absolute)
                                left("${x}%")
                                top("${y}%")
                                width(24.px)
                                height(24.px)
                                backgroundColor(Color(markerColor))
                                borderRadius(50.percent)
                                border(2.px, LineStyle.Solid, Color.Black)
                                cursor(Cursor.Pointer)
                                transform("translate(-50%, -50%)")
                                transition("transform 0.2s")
                            }
                            onMouseEnter {
                                it.target.style.transform = "translate(-50%, -50%) scale(1.5)"
                            }
                            onMouseLeave {
                                it.target.style.transform = "translate(-50%, -50%) scale(1)"
                            }
                            title(location.name)
                        }
                    )
                }
                
                // "My location" button
                Button(
                    attrs = {
                        // TODO: Get actual location
                        style {
                            position(Position.Absolute)
                            bottom(16.px)
                            right(16.px)
                            padding(12.px)
                            backgroundColor(Color("#1976d2"))
                            color(Color.White)
                            border(1.px, LineStyle.Solid, Color("#1565c0"))
                            borderRadius(50.percent)
                            cursor(Cursor.Pointer)
                            fontSize(20.px)
                            width(48.px)
                            height(48.px)
                            display(DisplayStyle.Flex)
                            justifyContent(JustifyContent.Center)
                            alignItems(AlignItems.Center)
                        }
                    }
                ) {
                    Text("📍")
                }
                
                // Legend
                Div(
                    attrs = {
                        style {
                            position(Position.Absolute)
                            top(16.px)
                            left(16.px)
                            padding(12.px)
                            backgroundColor(Color.White)
                            borderRadius(8.px)
                            boxShadow(0.px, 2.px, 8.px, Color("#00000033"))
                        }
                    }
                ) {
                    H4(
                        attrs = {
                            style {
                                margin(0.px, 0.px, 8.px, 0.px)
                                fontSize(14.px)
                            }
                        }
                    ) {
                        Text("Легенда:")
                    }
                    
                    LegendItemWeb("Спорт", "#f44336")
                    LegendItemWeb("Работа", "#2196f3")
                    LegendItemWeb("Отдых", "#4caf50")
                    LegendItemWeb("Образование", "#ffeb3b")
                    LegendItemWeb("Развлечения", "#9c27b0")
                }
            }
        }
    }
}

@Composable
fun LegendItemWeb(text: String, color: String) {
    Div(
        attrs = {
            style {
                display(DisplayStyle.Flex)
                alignItems(AlignItems.Center)
                gap(8.px)
                marginBottom(4.px)
            }
        }
    ) {
        Div(
            attrs = {
                style {
                    width(12.px)
                    height(12.px)
                    backgroundColor(Color(color))
                    borderRadius(50.percent)
                }
            }
        )
        
        Span(
            attrs = {
                style {
                    fontSize(12.px)
                    color(Color("#666"))
                }
            }
        ) {
            Text(text)
        }
    }
}
