package com.activemap.web.ui.components

import androidx.compose.runtime.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import com.activemap.shared.model.*

@Composable
fun EditLocationFormWeb(
    location: Location,
    onSave: (Location) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf(location.name) }
    var activityType by remember { mutableStateOf(location.activityType) }
    var latitude by remember { mutableStateOf(location.latitude.toString()) }
    var longitude by remember { mutableStateOf(location.longitude.toString()) }
    var coverage by remember { mutableStateOf(location.coverage) }
    var lighting by remember { mutableStateOf(location.lighting) }
    var inventory by remember { mutableStateOf(location.inventory) }
    var cleanliness by remember { mutableStateOf(location.cleanliness) }
    var noiseLevel by remember { mutableStateOf(location.noiseLevel) }
    var rating by remember { mutableStateOf(location.rating) }
    var status by remember { mutableStateOf(location.status) }
    var notes by remember { mutableStateOf(location.notes) }
    var photos by remember { mutableStateOf(location.photos.joinToString(", ")) }
    
    Div(
        attrs = {
            style {
                display(DisplayStyle.Flex)
                flexDirection(FlexDirection.Column)
                width(100.percent)
                overflow("auto")
            }
        }
    ) {
        // Name
        FormField("Название *") {
            Input(
                attrs = {
                    value(name)
                    onInput { event -> name = event.value }
                    style {
                        width(100.percent)
                        padding(12.px)
                        border(1.px, LineStyle.Solid, Color("#ccc"))
                        borderRadius(4.px)
                        fontSize(16.px)
                    }
                }
            )
        }
        
        // Activity type
        FormField("Тип активности") {
            Div(
                attrs = {
                    style {
                        display(DisplayStyle.Flex)
                        flexWrap(FlexWrap.Wrap)
                        gap(8.px)
                    }
                }
            ) {
                ActivityType.values().forEach { type ->
                    FilterChip(type.name, activityType == type) {
                        activityType = type
                    }
                }
            }
        }
        
        // Coordinates
        FormField("Координаты *") {
            Div(
                attrs = {
                    style {
                        display(DisplayStyle.Flex)
                        gap(16.px)
                    }
                }
            ) {
                Div(
                    attrs = {
                        style {
                            flex(1)
                        }
                    }
                ) {
                    Input(
                        attrs = {
                            value(latitude)
                            onInput { event -> latitude = event.value }
                            placeholder("Широта")
                            style {
                                width(100.percent)
                                padding(12.px)
                                border(1.px, LineStyle.Solid, Color("#ccc"))
                                borderRadius(4.px)
                                fontSize(16.px)
                            }
                        }
                    )
                }
                Div(
                    attrs = {
                        style {
                            flex(1)
                        }
                    }
                ) {
                    Input(
                        attrs = {
                            value(longitude)
                            onInput { event -> longitude = event.value }
                            placeholder("Долгота")
                            style {
                                width(100.percent)
                                padding(12.px)
                                border(1.px, LineStyle.Solid, Color("#ccc"))
                                borderRadius(4.px)
                                fontSize(16.px)
                            }
                        }
                    )
                }
            }
        }
        
        // Coverage
        FormField("Покрытие") {
            Div(
                attrs = {
                    style {
                        display(DisplayStyle.Flex)
                        flexWrap(FlexWrap.Wrap)
                        gap(8.px)
                    }
                }
            ) {
                CoverageLevel.values().forEach { level ->
                    FilterChip(level.name, coverage == level) {
                        coverage = level
                    }
                }
            }
        }
        
        // Lighting
        FormField("Освещение") {
            Div(
                attrs = {
                    style {
                        display(DisplayStyle.Flex)
                        flexWrap(FlexWrap.Wrap)
                        gap(8.px)
                    }
                }
            ) {
                LightingLevel.values().forEach { level ->
                    FilterChip(level.name, lighting == level) {
                        lighting = level
                    }
                }
            }
        }
        
        // Inventory
        FormField("Инвентарь") {
            Textarea(
                attrs = {
                    value(inventory)
                    onInput { event -> inventory = event.value }
                    style {
                        width(100.percent)
                        padding(12.px)
                        border(1.px, LineStyle.Solid, Color("#ccc"))
                        borderRadius(4.px)
                        fontSize(16.px)
                        minHeight(80.px)
                    }
                }
            )
        }
        
        // Cleanliness
        FormField("Чистота") {
            Div(
                attrs = {
                    style {
                        display(DisplayStyle.Flex)
                        flexWrap(FlexWrap.Wrap)
                        gap(8.px)
                    }
                }
            ) {
                CleanlinessLevel.values().forEach { level ->
                    FilterChip(level.name, cleanliness == level) {
                        cleanliness = level
                    }
                }
            }
        }
        
        // Noise level
        FormField("Уровень шума") {
            Div(
                attrs = {
                    style {
                        display(DisplayStyle.Flex)
                        flexWrap(FlexWrap.Wrap)
                        gap(8.px)
                    }
                }
            ) {
                NoiseLevel.values().forEach { level ->
                    FilterChip(level.name, noiseLevel == level) {
                        noiseLevel = level
                    }
                }
            }
        }
        
        // Rating
        FormField("Рейтинг: $rating/5") {
            Input(
                attrs = {
                    type(InputType.Range)
                    value(rating.toString())
                    onInput { event -> rating = event.value.toIntOrNull() ?: 3 }
                    min("1")
                    max("5")
                    step("1")
                    style {
                        width(100.percent)
                    }
                }
            )
        }
        
        // Status
        FormField("Статус") {
            Div(
                attrs = {
                    style {
                        display(DisplayStyle.Flex)
                        flexWrap(FlexWrap.Wrap)
                        gap(8.px)
                    }
                }
            ) {
                VisitStatus.values().forEach { s ->
                    FilterChip(s.name, status == s) {
                        status = s
                    }
                }
            }
        }
        
        // Notes
        FormField("Заметки") {
            Textarea(
                attrs = {
                    value(notes)
                    onInput { event -> notes = event.value }
                    style {
                        width(100.percent)
                        padding(12.px)
                        border(1.px, LineStyle.Solid, Color("#ccc"))
                        borderRadius(4.px)
                        fontSize(16.px)
                        minHeight(120.px)
                    }
                }
            )
        }
        
        // Photos
        FormField("Фото (через запятую)") {
            Input(
                attrs = {
                    value(photos)
                    onInput { event -> photos = event.value }
                    placeholder("URL1, URL2, URL3")
                    style {
                        width(100.percent)
                        padding(12.px)
                        border(1.px, LineStyle.Solid, Color("#ccc"))
                        borderRadius(4.px)
                        fontSize(16.px)
                    }
                }
            )
        }
        
        // Save button
        Button(
            attrs = {
                onClick {
                    if (name.isNotBlank() && latitude.isNotBlank() && longitude.isNotBlank()) {
                        val lat = latitude.toDoubleOrNull()
                        val lon = longitude.toDoubleOrNull()
                        if (lat != null && lon != null) {
                            val updatedLocation = location.copy(
                                name = name,
                                activityType = activityType,
                                latitude = lat,
                                longitude = lon,
                                coverage = coverage,
                                lighting = lighting,
                                inventory = inventory,
                                cleanliness = cleanliness,
                                noiseLevel = noiseLevel,
                                rating = rating,
                                status = status,
                                notes = notes,
                                photos = photos.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                                updatedAt = js("Date.now()").toLong()
                            )
                            onSave(updatedLocation)
                        }
                    }
                }
                style {
                    width(100.percent)
                    padding(16.px)
                    backgroundColor(Color("#4caf50"))
                    color(Color.White)
                    border(1.px, LineStyle.Solid, Color("#388e3c"))
                    borderRadius(4.px)
                    fontSize(18.px)
                    cursor(Cursor.Pointer)
                    marginTop(24.px)
                }
            }
        ) {
            Text("Сохранить изменения")
        }
        
        // Cancel button
        Button(
            attrs = {
                onClick { onCancel() }
                style {
                    width(100.percent)
                    padding(16.px)
                    backgroundColor(Color.White)
                    color(Color("#333"))
                    border(1.px, LineStyle.Solid, Color("#ccc"))
                    borderRadius(4.px)
                    fontSize(18.px)
                    cursor(Cursor.Pointer)
                    marginTop(8.px)
                }
            }
        ) {
            Text("Отмена")
        }
    }
}
