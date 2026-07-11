package com.activemap.web.ui.components

import androidx.compose.runtime.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import com.activemap.web.model.*

@Composable
fun MapViewWeb(
    locations: List<Location>,
    onLocationClick: (Location) -> Unit,
    onMapClick: (Double, Double) -> Unit = { _, _ -> },
    isRouteMode: Boolean = false,
    routeWaypoints: List<Pair<Double, Double>> = emptyList(),
    selectedRouteLocations: List<Pair<Double, Double>> = emptyList(),
    pickedPoint: Pair<Double, Double>? = null,
    currentRoute: Route? = null
) {
    val markerColors = mapOf(
        ActivityType.SPORT to "#f44336",
        ActivityType.WORK to "#2196f3",
        ActivityType.REST to "#4caf50",
        ActivityType.EDUCATION to "#ffeb3b",
        ActivityType.ENTERTAINMENT to "#9c27b0"
    )

    Div(attrs = { style { backgroundColor(Color("#e8e8e8")); minHeight(400.px) } }) {
        if (locations.isEmpty() && routeWaypoints.isEmpty() && pickedPoint == null) {
            Div(attrs = { style { padding(120.px); property("text-align", "center"); color(Color("#666666")); fontSize(20.px) } }) {
                Text(Strings.noLocations())
            }
        } else {
            // Show location markers as colored blocks
            locations.forEach { location ->
                Div(attrs = {
                    onClick { onLocationClick(location) }
                    style {
                        padding(8.px)
                        margin(4.px)
                        backgroundColor(Color(markerColors[location.activityType] ?: "#999999"))
                        color(Color("white"))
                        borderRadius(8.px)
                        property("cursor", "pointer")
                    }
                }) {
                    Text("${location.name} (${location.activityType.name})")
                }
            }
        }

        // Legend
        Div(attrs = { style { padding(12.px); margin(12.px); backgroundColor(Color("white")); borderRadius(8.px) } }) {
            H4(attrs = { style { margin(0.px, 0.px, 8.px, 0.px); fontSize(14.px) } }) { Text(Strings.legend()) }
            LegendItemWeb(Strings.activitySport(), "#f44336")
            LegendItemWeb(Strings.activityWork(), "#2196f3")
            LegendItemWeb(Strings.activityRest(), "#4caf50")
            LegendItemWeb(Strings.activityEducation(), "#ffeb3b")
            LegendItemWeb(Strings.activityEntertainment(), "#9c27b0")
        }
    }
}

@Composable
fun LegendItemWeb(text: String, color: String) {
    Div(attrs = { style { display(DisplayStyle.Flex); alignItems(AlignItems.Center); gap(8.px); marginBottom(4.px) } }) {
        Div(attrs = { style { width(12.px); height(12.px); backgroundColor(Color(color)); borderRadius(50.percent) } })
        Span(attrs = { style { fontSize(12.px); color(Color("#666666")) } }) { Text(text) }
    }
}
