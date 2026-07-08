package com.activemap.web.ui.components

import androidx.compose.runtime.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import com.activemap.shared.model.*

@Composable
fun AddLocationFormWeb(
    onSave: (Location) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var activityType by remember { mutableStateOf(ActivityType.SPORT) }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var coverage by remember { mutableStateOf(CoverageLevel.MEDIUM) }
    var lighting by remember { mutableStateOf(LightingLevel.MEDIUM) }
    var inventory by remember { mutableStateOf("") }
    var cleanliness by remember { mutableStateOf(CleanlinessLevel.MEDIUM) }
    var noiseLevel by remember { mutableStateOf(NoiseLevel.MEDIUM) }
    var rating by remember { mutableStateOf(3) }
    var status by remember { mutableStateOf(VisitStatus.WANT_TO_VISIT) }
    var notes by remember { mutableStateOf("") }
    var photos by remember { mutableStateOf("") }
    
    var showError by remember { mutableStateOf(false) }
    
    Div(
        attrs = {
            style {
                display(DisplayStyle.Flex)
                flexDirection(FlexDirection.Column)
                height(100.percent)
                width(100.percent)
                overflow("auto")
            }
        }
    ) {
        // Header
        Div(
            attrs = {
                style {
                    display(DisplayStyle.Flex)
                    justifyContent(JustifyContent.SpaceBetween)
                    alignItems(AlignItems.Center)
                    padding(16.px)
                    backgroundColor(Color("#f5f5f5"))
                    borderBottom(1.px, LineStyle.Solid, Color("#e0e0e0"))
                }
            }
        ) {
            H2(
                attrs = {
                    style {
                        margin(0.px)
                        color(Color("#333"))
                    }
                }
            ) {
                Text("Добавить локацию")
            }
            
            Button(
                attrs = {
                    onClick { onCancel() }
                    style {
                        padding(8.px, 16.px)
                        backgroundColor(Color("#f5f5f5"))
                        color(Color("#333"))
                        border(1.px, LineStyle.Solid, Color("#ccc"))
                        borderRadius(4.px)
                        cursor(Cursor.Pointer)
                    }
                }
            ) {
                Text("Закрыть")
            }
        }
        
        // Form
        Div(
            attrs = {
                style {
                    flex(1)
                    padding(16.px)
                    overflow("auto")
                }
            }
        ) {
            // Name (required)
            FormField("Название *", showError && name.isBlank()) {
                Input(
                    attrs = {
                        value(name)
                        onInput { event -> name = event.value }
                        style {
                            width(100.percent)
                            padding(12.px)
                            border(1.px, LineStyle.Solid, if (showError && name.isBlank()) Color("#f44336") else Color("#ccc"))
                            borderRadius(4.px)
                            fontSize(16.px)
                        }
                    }
                )
                if (showError && name.isBlank()) {
                    Span(
                        attrs = {
                            style {
                                color(Color("#f44336"))
                                fontSize(12.px)
                            }
                        }
                    ) {
                        Text("Название обязательно")
                    }
                }
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
            
            // Coordinates (required)
            FormField("Координаты *", showError && (latitude.isBlank() || longitude.isBlank())) {
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
                                    border(1.px, LineStyle.Solid, if (showError && latitude.isBlank()) Color("#f44336") else Color("#ccc"))
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
                                    border(1.px, LineStyle.Solid, if (showError && longitude.isBlank()) Color("#f44336") else Color("#ccc"))
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
                        showError = true
                        if (name.isNotBlank() && latitude.isNotBlank() && longitude.isNotBlank()) {
                            val lat = latitude.toDoubleOrNull()
                            val lon = longitude.toDoubleOrNull()
                            if (lat != null && lon != null) {
                                val location = Location(
                                    id = js("Date.now()").toString(),
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
                                    photos = photos.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                                )
                                onSave(location)
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
                Text("Сохранить")
            }
        }
    }
}

@Composable
fun FormField(
    label: String,
    hasError: Boolean = false,
    content: @Composable () -> Unit
) {
    Div(
        attrs = {
            style {
                marginBottom(24.px)
            }
        }
    ) {
        Label(
            attrs = {
                style {
                    display(DisplayStyle.Block)
                    marginBottom(8.px)
                    fontWeight("bold")
                    color(if (hasError) Color("#f44336") else Color("#333"))
                }
            }
        ) {
            Text(label)
        }
        content()
    }
}
