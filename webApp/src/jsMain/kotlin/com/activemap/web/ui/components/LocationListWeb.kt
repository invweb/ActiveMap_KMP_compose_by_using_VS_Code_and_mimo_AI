package com.activemap.web.ui.components

import androidx.compose.runtime.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import com.activemap.shared.model.*
import com.activemap.shared.model.LocationFilter

@Composable
fun LocationListWeb(
    locations: List<Location>,
    filter: LocationFilter,
    onFilterChange: (LocationFilter) -> Unit,
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
        // Search
        Input(
            attrs = {
                value(filter.searchQuery)
                onInput { event -> 
                    onFilterChange(filter.copy(searchQuery = event.value))
                }
                placeholder("Поиск по названию...")
                style {
                    padding(12.px)
                    margin(16.px)
                    width(100.percent)
                    border(1.px, LineStyle.Solid, Color("#ccc"))
                    borderRadius(4.px)
                    fontSize(16.px)
                }
            }
        )
        
        // Filter chips
        Div(
            attrs = {
                style {
                    display(DisplayStyle.Flex)
                    flexWrap(FlexWrap.Wrap)
                    gap(8.px)
                    padding(0.px, 16.px)
                    marginBottom(16.px)
                }
            }
        ) {
            FilterChip("Все типы", filter.activityType == null) {
                onFilterChange(filter.copy(activityType = null))
            }
            ActivityType.values().forEach { type ->
                FilterChip(type.name, filter.activityType == type) {
                    onFilterChange(filter.copy(activityType = type))
                }
            }
        }
        
        // Status filters
        Div(
            attrs = {
                style {
                    display(DisplayStyle.Flex)
                    flexWrap(FlexWrap.Wrap)
                    gap(8.px)
                    padding(0.px, 16.px)
                    marginBottom(16.px)
                }
            }
        ) {
            FilterChip("Все статусы", filter.status == null) {
                onFilterChange(filter.copy(status = null))
            }
            VisitStatus.values().forEach { status ->
                FilterChip(status.name, filter.status == status) {
                    onFilterChange(filter.copy(status = status))
                }
            }
        }
        
        // Location list
        Div(
            attrs = {
                style {
                    flex(1)
                    overflow("auto")
                    padding(16.px)
                }
            }
        ) {
            locations.forEach { location ->
                LocationListItemWeb(location) { onLocationClick(location) }
            }
        }
    }
}

@Composable
fun FilterChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Button(
        attrs = {
            onClick { onClick() }
            style {
                padding(8.px, 16.px)
                backgroundColor(if (selected) Color("#1976d2") else Color("#f5f5f5"))
                color(if (selected) Color.White else Color.Black)
                border(1.px, LineStyle.Solid, Color("#ccc"))
                borderRadius(16.px)
                cursor(Cursor.Pointer)
                fontSize(14.px)
            }
        }
    ) {
        Text(text)
    }
}

@Composable
fun LocationListItemWeb(location: Location, onClick: () -> Unit) {
    Div(
        attrs = {
            onClick { onClick() }
            style {
                padding(16.px)
                marginBottom(8.px)
                backgroundColor(Color.White)
                border(1.px, LineStyle.Solid, Color("#e0e0e0"))
                borderRadius(8.px)
                cursor(Cursor.Pointer)
                transition("background-color", 0.2.s)
            }
            onMouseEnter {
                it.target.style.backgroundColor = "#f5f5f5"
            }
            onMouseLeave {
                it.target.style.backgroundColor = "white"
            }
        }
    ) {
        H3(
            attrs = {
                style {
                    margin(0.px, 0.px, 8.px, 0.px)
                    fontSize(18.px)
                    color(Color("#333"))
                }
            }
        ) {
            Text(location.name)
        }
        
        P(
            attrs = {
                style {
                    margin(0.px, 0.px, 4.px, 0.px)
                    color(Color("#666"))
                }
            }
        ) {
            Text("Тип: ${location.activityType.name}")
        }
        
        P(
            attrs = {
                style {
                    margin(0.px, 0.px, 4.px, 0.px)
                    color(Color("#666"))
                }
            }
        ) {
            Text("Статус: ${location.status.name}")
        }
        
        P(
            attrs = {
                style {
                    margin(0.px)
                    color(Color("#666"))
                }
            }
        ) {
            Text("Рейтинг: ${location.rating}/5")
        }
    }
}
