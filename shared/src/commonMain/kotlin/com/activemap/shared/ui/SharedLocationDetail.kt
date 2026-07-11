package com.activemap.shared.ui

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
import com.activemap.shared.resources.Strings
import kotlinx.datetime.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedLocationDetail(
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
                        Icon(Icons.Default.ArrowBack, contentDescription = Strings.back())
                    }
                },
                actions = {
                    IconButton(onClick = { isEditing = !isEditing }) {
                        Icon(Icons.Default.Edit, contentDescription = Strings.edit())
                    }
                    IconButton(onClick = { onDelete(location) }) {
                        Icon(Icons.Default.Delete, contentDescription = Strings.delete())
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isEditing) {
            SharedEditLocationForm(
                location = editedLocation,
                onSave = { 
                    onUpdate(it)
                    isEditing = false
                },
                onCancel = { isEditing = false },
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            SharedLocationDetailContent(
                location = location,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
fun SharedLocationDetailContent(
    location: Location,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = location.name,
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${Strings.type()}: ${location.activityType.name}",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "${Strings.status()}: ${location.status.name}",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = Strings.coordinates(),
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "${Strings.latitude()}: ${location.latitude}",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "${Strings.longitude()}: ${location.longitude}",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = Strings.ratings(),
            style = MaterialTheme.typography.titleMedium
        )
        SharedRatingRow(Strings.coverage(), location.coverage.name)
        SharedRatingRow(Strings.lighting(), location.lighting.name)
        SharedRatingRow(Strings.cleanliness(), location.cleanliness.name)
        SharedRatingRow(Strings.noiseLevel(), location.noiseLevel.name)
        SharedRatingRow(Strings.rating(), "${location.rating}/5")
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = Strings.inventory(),
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = location.inventory.ifEmpty { Strings.notSpecified() },
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = Strings.notes(),
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = location.notes.ifEmpty { Strings.noNotes() },
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = Strings.photos(),
            style = MaterialTheme.typography.titleMedium
        )
        if (location.photos.isEmpty()) {
            Text(
                text = Strings.noPhotos(),
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
fun SharedRatingRow(label: String, value: String) {
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