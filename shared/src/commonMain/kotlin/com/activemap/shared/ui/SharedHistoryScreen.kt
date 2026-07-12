package com.activemap.shared.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.activemap.shared.model.LocationTrack
import com.activemap.shared.viewmodel.LocationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedHistoryScreen(
    viewModel: LocationViewModel,
    onTrackClick: (LocationTrack) -> Unit = {}
) {
    var showStartDialog by remember { mutableStateOf(false) }
    
    if (showStartDialog) {
        TrackNameDialog(
            onNameSelected = { name ->
                viewModel.startTracking(name)
                showStartDialog = false
            },
            onDismiss = { showStartDialog = false }
        )
    }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("История перемещений") },
                actions = {
                    IconButton(onClick = { showStartDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Начать запись"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Current track status
            CurrentTrackCard(viewModel)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "История треков",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            TrackList(viewModel, onTrackClick)
        }
    }
}

@Composable
private fun CurrentTrackCard(viewModel: LocationViewModel) {
    val currentTrack by viewModel.currentTrack.collectAsState()
    val isTracking by viewModel.isTracking.collectAsState()
    
    if (isTracking && currentTrack != null) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Активный трек",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    
                    IconButton(
                        onClick = { viewModel.stopTracking() },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = "Остановить",
                            tint = MaterialTheme.colorScheme.onError
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                currentTrack?.let { track ->
                    TrackInfoRow("Название", track.name)
                    TrackInfoRow("Длительность", formatDuration(track.startDate, track.endDate ?: System.currentTimeMillis()))
                    TrackInfoRow("Расстояние", formatDistance(track.distanceMeters))
                }
            }
        }
    }
}

@Composable
private fun TrackList(viewModel: LocationViewModel, onTrackClick: (LocationTrack) -> Unit) {
    val allTracks by viewModel.allTracks.collectAsState()
    
    if (allTracks.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Нет сохраненных треков",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    } else {
        LazyColumn {
            items(allTracks) { track ->
                TrackItem(track = track, onClick = { onTrackClick(track) })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun TrackItem(track: LocationTrack, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = track.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                if (track.endDate == null) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            TrackInfoRow("Дата", formatDateString(track.startDate))
            TrackInfoRow("Длительность", formatDuration(track.startDate, track.endDate ?: System.currentTimeMillis()))
            TrackInfoRow("Расстояние", formatDistance(track.distanceMeters))
        }
    }
}

@Composable
private fun TrackInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun TrackNameDialog(
    onNameSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("Мой трек ${System.currentTimeMillis() / 1000}") }
    
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Начать запись трека",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Название") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Отмена")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    TextButton(onClick = { onNameSelected(name) }) {
                        Text("Старт")
                    }
                }
            }
        }
    }
}

private fun formatDuration(start: Long, end: Long): String {
    val duration = end - start
    val hours = duration / 3600000
    val minutes = (duration % 3600000) / 60000
    val seconds = (duration % 60000) / 1000
    
    return buildString {
        if (hours > 0) append("$hours ч ")
        if (minutes > 0 || hours > 0) append("$minutes мин ")
        append("$seconds сек")
    }
}

private fun formatDistance(meters: Double): String {
    return if (meters >= 1000) {
        "${meters / 1000:.1f} км"
    } else {
        "${meters.toInt()} м"
    }
}

private fun formatDateString(timestamp: Long): String {
    val date = java.util.Date(timestamp)
    val sdf = java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", java.util.Locale.getDefault())
    return sdf.format(date)
}
