package com.activemap.shared.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.activemap.shared.model.*
import com.activemap.shared.resources.Strings
import kotlinx.datetime.Clock

private fun formatCoordinate(value: Double): String {
    val multiplied = (value * 1000000).toLong()
    val intPart = multiplied / 1000000
    val decimalPart = multiplied % 1000000
    val sign = if (value < 0) "-" else ""
    val absDecimal = if (decimalPart < 0) -decimalPart else decimalPart
    return "$sign$intPart.${absDecimal.toString().padStart(6, '0')}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedAddLocationForm(
    onSave: (Location) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    initialLat: Double? = null,
    initialLng: Double? = null
) {
    var name by remember { mutableStateOf("") }
    var activityType by remember { mutableStateOf(ActivityType.SPORT) }
    var latitude by remember { mutableStateOf(initialLat?.let { formatCoordinate(it) } ?: "") }
    var longitude by remember { mutableStateOf(initialLng?.let { formatCoordinate(it) } ?: "") }
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

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        TopAppBar(
            title = { Text(Strings.addLocation()) },
            navigationIcon = {
                IconButton(onClick = onCancel) {
                    Icon(Icons.Default.Close, contentDescription = Strings.close())
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(Strings.nameRequired()) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Text
                ),
                isError = showError && name.isBlank(),
                supportingText = if (showError && name.isBlank()) {
                    { Text(Strings.nameIsRequired()) }
                } else null
            )

            Text(Strings.activityType(), style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActivityType.entries.forEach { type ->
                    FilterChip(
                        selected = activityType == type,
                        onClick = { activityType = type },
                        label = { Text(type.name) }
                    )
                }
            }

            Text(Strings.coordinatesRequired(), style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = latitude,
                    onValueChange = { latitude = it },
                    modifier = Modifier.weight(1f),
                    label = { Text(Strings.latitude()) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    isError = showError && latitude.isBlank()
                )
                OutlinedTextField(
                    value = longitude,
                    onValueChange = { longitude = it },
                    modifier = Modifier.weight(1f),
                    label = { Text(Strings.longitude()) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    isError = showError && longitude.isBlank()
                )
            }

            Text(Strings.coverage(), style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CoverageLevel.entries.forEach { level ->
                    FilterChip(
                        selected = coverage == level,
                        onClick = { coverage = level },
                        label = { Text(level.name) }
                    )
                }
            }

            Text(Strings.lighting(), style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LightingLevel.entries.forEach { level ->
                    FilterChip(
                        selected = lighting == level,
                        onClick = { lighting = level },
                        label = { Text(level.name) }
                    )
                }
            }

            OutlinedTextField(
                value = inventory,
                onValueChange = { inventory = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(Strings.inventory()) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            Text(Strings.cleanliness(), style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CleanlinessLevel.entries.forEach { level ->
                    FilterChip(
                        selected = cleanliness == level,
                        onClick = { cleanliness = level },
                        label = { Text(level.name) }
                    )
                }
            }

            Text(Strings.noiseLevel(), style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NoiseLevel.entries.forEach { level ->
                    FilterChip(
                        selected = noiseLevel == level,
                        onClick = { noiseLevel = level },
                        label = { Text(level.name) }
                    )
                }
            }

            Text("${Strings.rating()}: $rating/5", style = MaterialTheme.typography.titleMedium)
            Slider(
                value = rating.toFloat(),
                onValueChange = { rating = it.toInt() },
                valueRange = 1f..5f,
                steps = 3
            )

            Text(Strings.status(), style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                VisitStatus.entries.forEach { s ->
                    FilterChip(
                        selected = status == s,
                        onClick = { status = s },
                        label = { Text(s.name) }
                    )
                }
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(Strings.notes()) },
                minLines = 3,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )

            OutlinedTextField(
                value = photos,
                onValueChange = { photos = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(Strings.photosCommaSeparated()) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            Button(
                onClick = {
                    showError = true
                    if (name.isNotBlank() && latitude.isNotBlank() && longitude.isNotBlank()) {
                        val lat = latitude.toDoubleOrNull()
                        val lon = longitude.toDoubleOrNull()
                        if (lat != null && lon != null) {
                            val location = Location(
                                id = Clock.System.now().toEpochMilliseconds().toString(),
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
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(Strings.save())
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}