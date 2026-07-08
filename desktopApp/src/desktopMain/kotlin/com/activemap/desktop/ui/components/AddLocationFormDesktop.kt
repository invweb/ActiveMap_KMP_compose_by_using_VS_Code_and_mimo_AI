package com.activemap.desktop.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.activemap.shared.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLocationFormDesktop(
    onSave: (Location) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
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
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Добавить локацию") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.Close, contentDescription = "Закрыть")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Name (required)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Название *") },
                isError = showError && name.isBlank(),
                supportingText = if (showError && name.isBlank()) {
                    { Text("Название обязательно") }
                } else null
            )
            
            // Activity type
            Text("Тип активности", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActivityType.values().forEach { type ->
                    FilterChip(
                        selected = activityType == type,
                        onClick = { activityType = type },
                        label = { Text(type.name) }
                    )
                }
            }
            
            // Coordinates (required)
            Text("Координаты *", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = latitude,
                    onValueChange = { latitude = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Широта") },
                    isError = showError && latitude.isBlank()
                )
                OutlinedTextField(
                    value = longitude,
                    onValueChange = { longitude = it },
                    modifier = Modifier.weight(1f),
                    label = { Text("Долгота") },
                    isError = showError && longitude.isBlank()
                )
            }
            
            // Coverage
            Text("Покрытие", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CoverageLevel.values().forEach { level ->
                    FilterChip(
                        selected = coverage == level,
                        onClick = { coverage = level },
                        label = { Text(level.name) }
                    )
                }
            }
            
            // Lighting
            Text("Освещение", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LightingLevel.values().forEach { level ->
                    FilterChip(
                        selected = lighting == level,
                        onClick = { lighting = level },
                        label = { Text(level.name) }
                    )
                }
            }
            
            // Inventory
            OutlinedTextField(
                value = inventory,
                onValueChange = { inventory = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Инвентарь") }
            )
            
            // Cleanliness
            Text("Чистота", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CleanlinessLevel.values().forEach { level ->
                    FilterChip(
                        selected = cleanliness == level,
                        onClick = { cleanliness = level },
                        label = { Text(level.name) }
                    )
                }
            }
            
            // Noise level
            Text("Уровень шума", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NoiseLevel.values().forEach { level ->
                    FilterChip(
                        selected = noiseLevel == level,
                        onClick = { noiseLevel = level },
                        label = { Text(level.name) }
                    )
                }
            }
            
            // Rating
            Text("Рейтинг: $rating/5", style = MaterialTheme.typography.titleMedium)
            Slider(
                value = rating.toFloat(),
                onValueChange = { rating = it.toInt() },
                valueRange = 1f..5f,
                steps = 3
            )
            
            // Status
            Text("Статус", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                VisitStatus.values().forEach { s ->
                    FilterChip(
                        selected = status == s,
                        onClick = { status = s },
                        label = { Text(s.name) }
                    )
                }
            }
            
            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Заметки") },
                minLines = 3
            )
            
            // Photos
            OutlinedTextField(
                value = photos,
                onValueChange = { photos = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Фото (через запятую)") }
            )
            
            // Save button
            Button(
                onClick = {
                    showError = true
                    if (name.isNotBlank() && latitude.isNotBlank() && longitude.isNotBlank()) {
                        val lat = latitude.toDoubleOrNull()
                        val lon = longitude.toDoubleOrNull()
                        if (lat != null && lon != null) {
                            val location = Location(
                                id = System.currentTimeMillis().toString(),
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
                Text("Сохранить")
            }
        }
    }
}
