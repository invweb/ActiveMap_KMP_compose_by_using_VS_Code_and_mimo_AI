package com.activemap.shared.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.activemap.shared.model.*
import com.activemap.shared.resources.Strings
import kotlinx.datetime.Clock

@Composable
fun SharedEditLocationForm(
    location: Location,
    onSave: (Location) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
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
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(Strings.nameRequired()) }
        )
        
        Text(Strings.activityType(), style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier.fillMaxWidth(),
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
                label = { Text(Strings.latitude()) }
            )
            OutlinedTextField(
                value = longitude,
                onValueChange = { longitude = it },
                modifier = Modifier.weight(1f),
                label = { Text(Strings.longitude()) }
            )
        }
        
        Text(Strings.coverage(), style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier.fillMaxWidth(),
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
            modifier = Modifier.fillMaxWidth(),
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
            label = { Text(Strings.inventory()) }
        )
        
        Text(Strings.cleanliness(), style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier.fillMaxWidth(),
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
            modifier = Modifier.fillMaxWidth(),
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
            modifier = Modifier.fillMaxWidth(),
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
            minLines = 3
        )
        
        OutlinedTextField(
            value = photos,
            onValueChange = { photos = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(Strings.photosCommaSeparated()) }
        )
        
        Button(
            onClick = {
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
                            updatedAt = Clock.System.now().toEpochMilliseconds()
                        )
                        onSave(updatedLocation)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(Strings.saveChanges())
        }
        
        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(Strings.cancel())
        }
    }
}