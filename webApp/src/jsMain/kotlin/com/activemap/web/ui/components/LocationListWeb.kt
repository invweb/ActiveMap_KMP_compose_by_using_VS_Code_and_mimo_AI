package com.activemap.web.ui.components

import androidx.compose.runtime.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import com.activemap.web.model.*


@Composable
fun LocationListWeb(
    locations: List<Location>,
    filter: LocationFilter,
    onFilterChange: (LocationFilter) -> Unit,
    onLocationClick: (Location) -> Unit
) {
    var search by remember { mutableStateOf(filter.searchQuery) }

    Div(attrs = {
        style {
            property("padding", "16px")
            property("overflow-y", "auto")
            property("height", "100%")
        }
    }) {
        RawInput(search, { search = it; onFilterChange(filter.copy(searchQuery = it)) }, "text", Strings.searchPlaceholder())

        Div(attrs = { style { property("display", "flex"); property("flex-wrap", "wrap"); property("gap", "8px"); property("margin-bottom", "12px") } }) {
            FilterChip(Strings.allTypes(), filter.activityType == null) { onFilterChange(filter.copy(activityType = null)) }
            ActivityType.values().forEach { type -> FilterChip(type.name, filter.activityType == type) { onFilterChange(filter.copy(activityType = type)) } }
        }

        Div(attrs = { style { property("display", "flex"); property("flex-wrap", "wrap"); property("gap", "8px"); property("margin-bottom", "16px") } }) {
            FilterChip(Strings.allStatuses(), filter.status == null) { onFilterChange(filter.copy(status = null)) }
            VisitStatus.values().forEach { s -> FilterChip(s.name, filter.status == s) { onFilterChange(filter.copy(status = s)) } }
        }

        locations.forEach { location ->
            Div(attrs = {
                onClick { onLocationClick(location) }
                style { property("padding", "16px"); property("margin-bottom", "8px"); property("background-color", "white"); property("border", "1px solid #e0e0e0"); property("border-radius", "8px"); property("cursor", "pointer") }
            }) {
                H3(attrs = { style { property("margin", "0 0 8px 0"); property("font-size", "18px"); property("color", "#333") } }) { Text(location.name) }
                P(attrs = { style { property("margin", "0 0 4px 0"); property("color", "#666") } }) { Text("${Strings.type()}: ${location.activityType.name}") }
                P(attrs = { style { property("margin", "0 0 4px 0"); property("color", "#666") } }) { Text("${Strings.status()}: ${location.status.name}") }
                P(attrs = { style { property("margin", "0"); property("color", "#666") } }) { Text("${Strings.rating()}: ${location.rating}/5") }
            }
        }
    }
}

@Composable
fun FilterChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Button(attrs = {
        onClick { onClick() }
        style {
            property("padding", "8px 16px")
            property("background-color", if (selected) "#1976d2" else "#f5f5f5")
            property("color", if (selected) "white" else "black")
            property("border", "1px solid #ccc")
            property("border-radius", "16px")
            property("cursor", "pointer")
            property("font-size", "14px")
        }
    }) { Text(text) }
}
