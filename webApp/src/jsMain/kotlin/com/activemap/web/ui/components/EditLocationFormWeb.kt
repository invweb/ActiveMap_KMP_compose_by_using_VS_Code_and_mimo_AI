package com.activemap.web.ui.components

import androidx.compose.runtime.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import com.activemap.web.model.*
import com.activemap.web.model.Strings


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

    Div(attrs = {
        style { display(DisplayStyle.Flex); flexDirection(FlexDirection.Column); width(100.percent); property("overflow", "auto") }
    }) {
        FormField(Strings.nameRequired()) { RawInput(name, { name = it }) }
        FormField(Strings.activityType()) {
            Div(attrs = { style { display(DisplayStyle.Flex); flexWrap(FlexWrap.Wrap); gap(8.px) } }) {
                ActivityType.values().forEach { type -> FilterChip(type.name, activityType == type) { activityType = type } }
            }
        }
        FormField(Strings.coordinatesRequired()) {
            Div(attrs = { style { display(DisplayStyle.Flex); gap(16.px) } }) {
                Div(attrs = { style { flex(1) } }) { RawInput(latitude, { latitude = it }, "text", Strings.latitude()) }
                Div(attrs = { style { flex(1) } }) { RawInput(longitude, { longitude = it }, "text", Strings.longitude()) }
            }
        }
        FormField(Strings.coverage()) {
            Div(attrs = { style { display(DisplayStyle.Flex); flexWrap(FlexWrap.Wrap); gap(8.px) } }) {
                CoverageLevel.values().forEach { level -> FilterChip(level.name, coverage == level) { coverage = level } }
            }
        }
        FormField(Strings.lighting()) {
            Div(attrs = { style { display(DisplayStyle.Flex); flexWrap(FlexWrap.Wrap); gap(8.px) } }) {
                LightingLevel.values().forEach { level -> FilterChip(level.name, lighting == level) { lighting = level } }
            }
        }
        FormField(Strings.inventory()) { RawTextArea(inventory, { inventory = it }) }
        FormField(Strings.cleanliness()) {
            Div(attrs = { style { display(DisplayStyle.Flex); flexWrap(FlexWrap.Wrap); gap(8.px) } }) {
                CleanlinessLevel.values().forEach { level -> FilterChip(level.name, cleanliness == level) { cleanliness = level } }
            }
        }
        FormField(Strings.noiseLevel()) {
            Div(attrs = { style { display(DisplayStyle.Flex); flexWrap(FlexWrap.Wrap); gap(8.px) } }) {
                NoiseLevel.values().forEach { level -> FilterChip(level.name, noiseLevel == level) { noiseLevel = level } }
            }
        }
        FormField("${Strings.rating()}: $rating/5") { RawInput(rating.toString(), { rating = it.toIntOrNull() ?: 3 }, "range") }
        FormField(Strings.status()) {
            Div(attrs = { style { display(DisplayStyle.Flex); flexWrap(FlexWrap.Wrap); gap(8.px) } }) {
                VisitStatus.values().forEach { s -> FilterChip(s.name, status == s) { status = s } }
            }
        }
        FormField(Strings.notes()) { RawTextArea(notes, { notes = it }) }
        FormField(Strings.photosCommaSeparated()) { RawInput(photos, { photos = it }, "text", "URL1, URL2, URL3") }
        Button(attrs = {
            onClick {
                if (name.isNotBlank() && latitude.isNotBlank() && longitude.isNotBlank()) {
                    val lat = latitude.toDoubleOrNull(); val lon = longitude.toDoubleOrNull()
                    if (lat != null && lon != null) {
                        onSave(location.copy(
                            name = name, activityType = activityType, latitude = lat, longitude = lon,
                            coverage = coverage, lighting = lighting, inventory = inventory,
                            cleanliness = cleanliness, noiseLevel = noiseLevel, rating = rating,
                            status = status, notes = notes,
                            photos = photos.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                            updatedAt = js("Date.now()").toLong()
                        ))
                    }
                }
            }
            style {
                width(100.percent); padding(16.px); backgroundColor(Color("#4caf50"))
                color(Color("white")); border(1.px, LineStyle.Solid, Color("#388e3c"))
                borderRadius(4.px); fontSize(18.px); property("cursor", "pointer"); marginTop(24.px)
            }
        }) { Text(Strings.saveChanges()) }
        Button(attrs = {
            onClick { onCancel() }
            style {
                width(100.percent); padding(16.px); backgroundColor(Color("white"))
                color(Color("#333")); border(1.px, LineStyle.Solid, Color("#ccc"))
                borderRadius(4.px); fontSize(18.px); property("cursor", "pointer"); marginTop(8.px)
            }
        }) { Text(Strings.cancel()) }
    }
}
