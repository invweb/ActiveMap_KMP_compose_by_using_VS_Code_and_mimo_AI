package com.activemap.web.ui.components

import androidx.compose.runtime.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import com.activemap.web.model.*
import com.activemap.web.model.Strings


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

    Div(attrs = {
        style {
            display(DisplayStyle.Flex); flexDirection(FlexDirection.Column)
            height(100.percent); width(100.percent); property("overflow", "auto")
        }
    }) {
        Div(attrs = {
            style {
                display(DisplayStyle.Flex); justifyContent(JustifyContent.SpaceBetween)
                alignItems(AlignItems.Center); padding(16.px)
                backgroundColor(Color("#f5f5f5")); property("border-bottom", "1px solid #e0e0e0")
            }
        }) {
            H2(attrs = { style { margin(0.px); color(Color("#333")) } }) { Text(Strings.addLocation()) }
            Button(attrs = {
                onClick { onCancel() }
                style {
                    padding(8.px, 16.px); backgroundColor(Color("#f5f5f5"))
                    color(Color("#333")); border(1.px, LineStyle.Solid, Color("#ccc"))
                    borderRadius(4.px); property("cursor", "pointer")
                }
            }) { Text(Strings.close()) }
        }

        Div(attrs = { style { flex(1); padding(16.px); property("overflow", "auto") } }) {
            FormField(Strings.nameRequired(), showError && name.isBlank()) {
                RawInput(name, { name = it }, "text")
                if (showError && name.isBlank()) {
                    Span(attrs = { style { color(Color("#f44336")); fontSize(12.px) } }) { Text(Strings.nameIsRequired()) }
                }
            }
            FormField(Strings.activityType()) {
                Div(attrs = { style { display(DisplayStyle.Flex); flexWrap(FlexWrap.Wrap); gap(8.px) } }) {
                    ActivityType.values().forEach { type -> FilterChip(type.name, activityType == type) { activityType = type } }
                }
            }
            FormField(Strings.coordinatesRequired(), showError && (latitude.isBlank() || longitude.isBlank())) {
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
            FormField("${Strings.rating()}: $rating/5") {
                RawInput(rating.toString(), { rating = it.toIntOrNull() ?: 3 }, "range")
            }
            FormField(Strings.status()) {
                Div(attrs = { style { display(DisplayStyle.Flex); flexWrap(FlexWrap.Wrap); gap(8.px) } }) {
                    VisitStatus.values().forEach { s -> FilterChip(s.name, status == s) { status = s } }
                }
            }
            FormField(Strings.notes()) { RawTextArea(notes, { notes = it }) }
            FormField(Strings.photosCommaSeparated()) { RawInput(photos, { photos = it }, "text", "URL1, URL2, URL3") }

            Button(attrs = {
                onClick {
                    showError = true
                    if (name.isNotBlank() && latitude.isNotBlank() && longitude.isNotBlank()) {
                        val lat = latitude.toDoubleOrNull(); val lon = longitude.toDoubleOrNull()
                        if (lat != null && lon != null) {
                            onSave(Location(
                                id = js("Date.now()").toString(), name = name, activityType = activityType,
                                latitude = lat, longitude = lon, coverage = coverage, lighting = lighting,
                                inventory = inventory, cleanliness = cleanliness, noiseLevel = noiseLevel,
                                rating = rating, status = status, notes = notes,
                                photos = photos.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                            ))
                        }
                    }
                }
                style {
                    width(100.percent); padding(16.px); backgroundColor(Color("#4caf50"))
                    color(Color("white")); border(1.px, LineStyle.Solid, Color("#388e3c"))
                    borderRadius(4.px); fontSize(18.px); property("cursor", "pointer"); marginTop(24.px)
                }
            }) { Text(Strings.save()) }
        }
    }
}

@Composable
fun FormField(label: String, hasError: Boolean = false, content: @Composable () -> Unit) {
    Div(attrs = { style { marginBottom(24.px) } }) {
        Label(attrs = {
            style {
                display(DisplayStyle.Block); marginBottom(8.px); fontWeight(700)
                color(if (hasError) Color("#f44336") else Color("#333"))
            }
        }) { Text(label) }
        content()
    }
}

@Composable
fun RawInput(value: String, onValueChange: (String) -> Unit, type: String = "text", placeholder: String = "") {
    var el: dynamic = null
    LaunchedEffect(value) { el?.asDynamic()?.value = value }
    @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
    Div(attrs = {
        ref { div ->
            val input = js("document.createElement('input')")
            input.type = type
            input.value = value
            if (placeholder.isNotEmpty()) input.placeholder = placeholder
            input.style.cssText = "width:100%;padding:12px;border:1px solid #ccc;border-radius:4px;font-size:16px;box-sizing:border-box;"
            input.oninput = { onValueChange(input.value as String) }
            div.asDynamic().appendChild(input)
            el = input
            onDispose { div.asDynamic().removeChild(input) }
        }
    })
}

@Composable
fun RawTextArea(value: String, onValueChange: (String) -> Unit) {
    var el: dynamic = null
    LaunchedEffect(value) { el?.asDynamic()?.value = value }
    @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
    Div(attrs = {
        ref { div ->
            val ta = js("document.createElement('textarea')")
            ta.value = value
            ta.style.cssText = "width:100%;padding:12px;border:1px solid #ccc;border-radius:4px;font-size:16px;min-height:80px;box-sizing:border-box;"
            ta.oninput = { onValueChange(ta.value as String) }
            div.asDynamic().appendChild(ta)
            el = ta
            onDispose { div.asDynamic().removeChild(ta) }
        }
    })
}
