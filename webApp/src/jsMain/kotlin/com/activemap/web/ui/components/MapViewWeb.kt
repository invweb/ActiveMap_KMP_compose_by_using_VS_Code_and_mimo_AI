package com.activemap.web.ui.components

import androidx.compose.runtime.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import com.activemap.shared.model.ActivityType
import com.activemap.shared.model.Location
import com.activemap.shared.model.Route

@Composable
fun MapViewWeb(
    locations: List<Location>,
    onLocationClick: (Location) -> Unit,
    onMapClick: (Double, Double) -> Unit = { _, _ -> },
    isRouteMode: Boolean = false,
    routeStart: Pair<Double, Double>? = null,
    routeEnd: Pair<Double, Double>? = null,
    currentRoute: Route? = null
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
        Div(
            attrs = {
                style {
                    flex(1)
                    position(Position.Relative)
                    backgroundColor(Color("#f0f0f0"))
                    overflow("hidden")
                }
                if (isRouteMode) {
                    onClick { event ->
                        val rect = event.target.getBoundingClientRect()
                        val x = (event.clientX - rect.left) / rect.width * 100
                        val y = (event.clientY - rect.top) / rect.height * 100
                        onMapClick(x, y)
                    }
                }
            }
        ) {
            val allPoints = mutableListOf<Pair<Double, Double>>()
            locations.forEach { allPoints.add(it.latitude to it.longitude) }
            routeStart?.let { allPoints.add(it) }
            routeEnd?.let { allPoints.add(it) }
            currentRoute?.points?.forEach { allPoints.add(it.latitude to it.longitude) }
            
            if (allPoints.isEmpty()) {
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
                val minLat = allPoints.minOf { it.first }
                val maxLat = allPoints.maxOf { it.first }
                val minLon = allPoints.minOf { it.second }
                val maxLon = allPoints.maxOf { it.second }
                
                val latRange = (maxLat - minLat).coerceAtLeast(0.001)
                val lonRange = (maxLon - minLon).coerceAtLeast(0.001)
                
                currentRoute?.let { route ->
                    if (route.points.size >= 2) {
                        val pathPoints = route.points.joinToString(" ") { point ->
                            val x = ((point.longitude - minLon) / lonRange * 100).coerceIn(5.0, 95.0)
                            val y = ((maxLat - point.latitude) / latRange * 100).coerceIn(5.0, 95.0)
                            "${x}%,${y}%"
                        }
                        
                        Svg(
                            attrs = {
                                style {
                                    position(Position.Absolute)
                                    top(0.px)
                                    left(0.px)
                                    width(100.percent)
                                    height(100.percent)
                                    pointerEvents("none")
                                }
                            }
                        ) {
                            Polyline(
                                attrs = {
                                    attr("points", route.points.joinToString(" ") { point ->
                                        val x = ((point.longitude - minLon) / lonRange * 100).coerceIn(5.0, 95.0)
                                        val y = ((maxLat - point.latitude) / latRange * 100).coerceIn(5.0, 95.0)
                                        "${x * 10},${y * 10}"
                                    })
                                    attr("fill", "none")
                                    attr("stroke", "#2196f3")
                                    attr("stroke-width", "4")
                                }
                            )
                        }
                    }
                }
                
                routeStart?.let { start ->
                    val x = ((start.second - minLon) / lonRange * 100).coerceIn(5.0, 95.0)
                    val y = ((maxLat - start.first) / latRange * 100).coerceIn(5.0, 95.0)
                    
                    Div(
                        attrs = {
                            style {
                                position(Position.Absolute)
                                left("${x}%")
                                top("${y}%")
                                width(20.px)
                                height(20.px)
                                backgroundColor(Color("#4caf50"))
                                borderRadius(50.percent)
                                border(2.px, LineStyle.Solid, Color.Black)
                                transform("translate(-50%, -50%)")
                                zIndex(11)
                            }
                            title("Начало маршрута")
                        }
                    )
                }
                
                routeEnd?.let { end ->
                    val x = ((end.second - minLon) / lonRange * 100).coerceIn(5.0, 95.0)
                    val y = ((maxLat - end.first) / latRange * 100).coerceIn(5.0, 95.0)
                    
                    Div(
                        attrs = {
                            style {
                                position(Position.Absolute)
                                left("${x}%")
                                top("${y}%")
                                width(20.px)
                                height(20.px)
                                backgroundColor(Color("#f44336"))
                                borderRadius(50.percent)
                                border(2.px, LineStyle.Solid, Color.Black)
                                transform("translate(-50%, -50%)")
                                zIndex(11)
                            }
                            title("Конец маршрута")
                        }
                    )
                }
                
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
                                zIndex(10)
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
                
                Button(
                    attrs = {
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
                    
                    if (isRouteMode) {
                        Spacer(attrs = { style { height(8.px) } })
                        LegendItemWeb("Начало", "#4caf50")
                        LegendItemWeb("Конец", "#f44336")
                        LegendItemWeb("Маршрут", "#2196f3")
                    }
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
