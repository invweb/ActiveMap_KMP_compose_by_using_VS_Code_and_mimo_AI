package com.activemap.desktop.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.activemap.shared.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationDetailDesktop(
    location: Location,
    onBack: () -> Unit,
    onUpdate: (Location) -> Unit,
    onDelete: (Location) -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedLocation by remember { mutableStateOf(location) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(location.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = { isEditing = !isEditing }) {
                        Icon(Icons.Default.Edit, contentDescription = "Редактировать")
                    }
                    IconButton(onClick = { onDelete(location) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Удалить")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isEditing) {
            EditLocationFormDesktop(
                location = editedLocation,
                onSave = { 
                    onUpdate(it)
                    isEditing = false
                },
                onCancel = { isEditing = false },
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            LocationDetailContentDesktop(
                location = location,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
fun LocationDetailContentDesktop(
    location: Location,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Basic info
        Text(
            text = location.name,
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Тип: ${location.activityType.name}",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Статус: ${location.status.name}",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        // Coordinates
        Text(
            text = "Координаты",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "Широта: ${location.latitude}",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Долгота: ${location.longitude}",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        // Ratings
        Text(
            text = "Оценки",
            style = MaterialTheme.typography.titleMedium
        )
        RatingRow("Покрытие", location.coverage.name)
        RatingRow("Освещение", location.lighting.name)
        RatingRow("Чистота", location.cleanliness.name)
        RatingRow("Шум", location.noiseLevel.name)
        RatingRow("Рейтинг", "${location.rating}/5")
        Spacer(modifier = Modifier.height(16.dp))
        
        // Inventory
        Text(
            text = "Инвентарь",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = location.inventory.ifEmpty { "Не указан" },
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        // Notes
        Text(
            text = "Заметки",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = location.notes.ifEmpty { "Нет заметок" },
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        // Photos
        Text(
            text = "Фото",
            style = MaterialTheme.typography.titleMedium
        )
        if (location.photos.isEmpty()) {
            Text(
                text = "Нет фото",
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            location.photos.forEach { photoUrl ->
                Text(
                    text = photoUrl,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun RatingRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}
